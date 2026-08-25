variable "aws_region" {
  description = "AWS region to create the 3 VPS/EC2 instances." # Mô tả biến region
  type        = string                                           # Kiểu chuỗi
  default     = "ap-southeast-1"                                 # Singapore
}

variable "project_name" {
  type    = string        # Kiểu chuỗi
  default = "web-bansach" # Tên dự án dùng để đặt tên tài nguyên
}

variable "instance_type" {
  description = "Use at least 2GB RAM per node for k3s demo workloads." # Gợi ý cấu hình VPS
  type        = string                                                  # Kiểu chuỗi
  default     = "t3.small"                                              # Loại EC2 cho k3s
}

variable "key_name" {
  description = "Existing AWS EC2 key pair name." # Tên key pair AWS
  type        = string                            # Kiểu chuỗi
}

variable "ssh_public_key_path" {
  description = "Local public key path used by Ansible SSH." # Đường dẫn public key local
  type        = string                                       # Kiểu chuỗi
  default     = "~/.ssh/id_rsa.pub"                          # Giá trị mặc định
}

variable "allowed_ssh_cidr" {
  description = "Your public IP CIDR for SSH, for example 203.0.113.10/32." # IP được phép SSH/kubectl
  type        = string                                                      # Kiểu chuỗi
}

variable "root_volume_size" {
  type    = number # Kiểu số
  default = 30     # Dung lượng ổ đĩa GB
}
