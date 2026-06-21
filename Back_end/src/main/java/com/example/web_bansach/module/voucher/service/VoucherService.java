package com.example.web_bansach.module.voucher.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.voucher.dto.request.CreateVoucherRequest;
import com.example.web_bansach.module.voucher.dto.response.VoucherResponse;
import com.example.web_bansach.module.voucher.entity.Voucher;
import com.example.web_bansach.module.voucher.entity.UserVoucher;
import com.example.web_bansach.module.voucher.mapper.VoucherMapper;
import com.example.web_bansach.module.voucher.repository.UserVoucherRepository;
import com.example.web_bansach.module.voucher.repository.VoucherRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

/**
 * Service quản lý voucher
 * Sử dụng constructor injection thay vì field injection
 */
@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    public VoucherService(VoucherRepository voucherRepository,
            UserVoucherRepository userVoucherRepository,
            UserRepository userRepository,
            VoucherMapper voucherMapper) {
        this.voucherRepository = voucherRepository;
        this.userVoucherRepository = userVoucherRepository;
        this.userRepository = userRepository;
        this.voucherMapper = voucherMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse claimVoucher(String email, Long voucherId) {
        Users user = requireUser(email);
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher"));

        // Lock by code before checking quantity so concurrent claims cannot oversell.
        voucher = voucherRepository.findByCodeForUpdate(voucher.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher"));
        if (userVoucherRepository.existsByUser_IdAndVoucher_Id(user.getId(), voucher.getId())) {
            throw new BusinessException("Bạn đã lấy voucher này rồi");
        }
        validateAvailable(voucher);

        UserVoucher ownedVoucher = new UserVoucher();
        ownedVoucher.setUser(user);
        ownedVoucher.setVoucher(voucher);
        ownedVoucher.setClaimedAt(LocalDateTime.now());
        ownedVoucher.setUsed(false);
        userVoucherRepository.save(ownedVoucher);

        voucher.setQuantity(voucher.getQuantity() - 1);
        voucher.setUpdatedAt(LocalDateTime.now());
        voucherRepository.save(voucher);
        return voucherMapper.mapToResponse(voucher);
    }

    @Transactional(readOnly = true)
    public Page<VoucherResponse> getMyAvailableVouchers(String email, int page, int size) {
        Users user = requireUser(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by("expiredAt").ascending());
        return userVoucherRepository.findAvailableByUserId(user.getId(), LocalDate.now(), pageable)
                .map(voucherMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<VoucherResponse> getMyClaimedVouchers(String email, int page, int size) {
        Users user = requireUser(email);
        return userVoucherRepository.findClaimedByUserId(user.getId(), PageRequest.of(page, size))
                .map(voucherMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public VoucherResponse getMyVoucherByCode(String email, String code) {
        Users user = requireUser(email);
        UserVoucher ownedVoucher = userVoucherRepository
                .findByUser_IdAndVoucher_CodeIgnoreCaseAndUsedFalse(user.getId(), normalizeCode(code))
                .orElseThrow(() -> new BusinessException("Voucher không thuộc tài khoản hoặc đã được sử dụng"));
        validateNotExpired(ownedVoucher.getVoucher());
        return voucherMapper.mapToResponse(ownedVoucher.getVoucher());
    }

    /**
     * Tạo voucher mới (admin)
     */
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        String code = normalizeCode(request.getCode());

        // Kiểm tra mã voucher đã tồn tại chưa
        if (voucherRepository.existsByCode(code)) {
            throw new BusinessException("Mã voucher này đã tồn tại");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setQuantity(request.getQuantity());
        voucher.setExpiredAt(request.getExpiredAt());
        voucher.setCreatedAt(LocalDateTime.now());
        voucher.setUpdatedAt(LocalDateTime.now());

        Voucher savedVoucher = voucherRepository.save(voucher);
        return voucherMapper.mapToResponse(savedVoucher);
    }

    /**
     * Cập nhật voucher (admin)
     */
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse updateVoucher(Long voucherId, CreateVoucherRequest request) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher"));
        String code = normalizeCode(request.getCode());

        // Nếu thay đổi code, kiểm tra xem code mới đã tồn tại chưa
        if (!voucher.getCode().equals(code) && voucherRepository.existsByCode(code)) {
            throw new BusinessException("Mã voucher này đã tồn tại");
        }

        voucher.setCode(code);
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setQuantity(request.getQuantity());
        voucher.setExpiredAt(request.getExpiredAt());
        voucher.setUpdatedAt(LocalDateTime.now());

        Voucher updatedVoucher = voucherRepository.save(voucher);
        return voucherMapper.mapToResponse(updatedVoucher);
    }

    /**
     * Xóa voucher (admin)
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteVoucher(Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher"));
        voucherRepository.delete(voucher);
    }

    /**
     * Lấy chi tiết voucher bằng code
     */
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(normalizeCode(code))
                .orElseThrow(() -> new ResourceNotFoundException("Mã voucher không hợp lệ"));

        // Kiểm tra voucher có hợp lệ không
        if (voucher.getExpiredAt().isBefore(LocalDate.now())) {
            throw new BusinessException("Voucher này đã hết hạn");
        }

        if (voucher.getQuantity() <= 0) {
            throw new BusinessException("Voucher này đã hết lượt sử dụng");
        }

        return voucherMapper.mapToResponse(voucher);
    }

    /**
     * Lấy chi tiết voucher theo ID
     */
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherDetail(Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher"));
        return voucherMapper.mapToResponse(voucher);
    }

    /**
     * Lấy danh sách voucher hợp lệ (user - chỉ show voucher còn hạn sử dụng)
     */
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getValidVouchers(int page, int size) {
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(page, size, Sort.by("expiredAt").ascending());
        return voucherRepository.findValidVouchers(today, pageable)
                .map(voucherMapper::mapToResponse);
    }

    /**
     * Lấy danh sách tất cả voucher (admin)
     */
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getAllVouchers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return voucherRepository.findAll(pageable)
                .map(voucherMapper::mapToResponse);
    }

    /**
     * Lấy danh sách voucher hết hạn (admin)
     */
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getExpiredVouchers(int page, int size) {
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(page, size, Sort.by("expiredAt").descending());
        return voucherRepository.findExpiredVouchers(today, pageable)
                .map(voucherMapper::mapToResponse);
    }

    /**
     * Giảm số lượng voucher (khi user sử dụng)
     */
    @Transactional(rollbackFor = Exception.class)
    public void useVoucher(String code) {
        Voucher voucher = voucherRepository.findByCodeForUpdate(normalizeCode(code))
                .orElseThrow(() -> new ResourceNotFoundException("Mã voucher không hợp lệ"));

        if (voucher.getExpiredAt().isBefore(LocalDate.now())) {
            throw new BusinessException("Voucher này đã hết hạn");
        }

        if (voucher.getQuantity() <= 0) {
            throw new BusinessException("Voucher này đã hết lượt sử dụng");
        }

        voucher.setQuantity(voucher.getQuantity() - 1);
        voucher.setUpdatedAt(LocalDateTime.now());
        voucherRepository.save(voucher);
    }

    @Transactional(rollbackFor = Exception.class)
    public void useOwnedVoucher(String email, String code) {
        Users user = requireUser(email);
        UserVoucher ownedVoucher = userVoucherRepository
                .findUnusedForUpdate(user.getId(), normalizeCode(code))
                .orElseThrow(() -> new BusinessException("Voucher không thuộc tài khoản hoặc đã được sử dụng"));
        validateNotExpired(ownedVoucher.getVoucher());
        ownedVoucher.setUsed(true);
        ownedVoucher.setUsedAt(LocalDateTime.now());
        userVoucherRepository.save(ownedVoucher);
    }

    private Users requireUser(String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        return user;
    }

    private void validateAvailable(Voucher voucher) {
        validateNotExpired(voucher);
        if (voucher.getQuantity() == null || voucher.getQuantity() <= 0) {
            throw new BusinessException("Voucher này đã hết lượt nhận");
        }
    }

    private void validateNotExpired(Voucher voucher) {
        if (voucher.getExpiredAt() == null || voucher.getExpiredAt().isBefore(LocalDate.now())) {
            throw new BusinessException("Voucher này đã hết hạn");
        }
    }

    private String normalizeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("Mã voucher không được để trống");
        }
        return code.trim().toUpperCase();
    }
}
