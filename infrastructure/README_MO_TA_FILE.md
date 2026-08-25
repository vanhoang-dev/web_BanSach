# Mo Ta Cac File Trong Thu Muc Infrastructure

File này giải thích tên các file trong thư mục `infrastructure` và mục đích sử dụng của từng file. Thư mục này dùng để tạo hạ tầng AWS, cài Kubernetes k3s, deploy ứng dụng Web Bán Sách và cấu hình monitoring.

## 1. File tổng quan

| File | Dùng để làm gì |
| --- | --- |
| `README.md` | Hướng dẫn các bước chạy server, tạo hạ tầng, cài k3s, deploy ứng dụng, kiểm tra website, debug và xóa hạ tầng. |
| `README_MO_TA_FILE.md` | File hiện tại, dùng để mô tả ý nghĩa và công dụng của các file trong `infrastructure`. |

## 2. Thư mục `terraform`

Thư mục `terraform` dùng để khai báo và tạo tài nguyên AWS như VPC, subnet, security group và các VPS EC2 chạy cụm k3s.

| File | Dùng để làm gì |
| --- | --- |
| `terraform/versions.tf` | Khai báo phiên bản Terraform và provider cần dùng, ví dụ provider AWS và local. |
| `terraform/variables.tf` | Khai báo các biến đầu vào cho Terraform như region AWS, tên project, loại EC2, SSH key, dung lượng ổ đĩa và IP được phép SSH. |
| `terraform/terraform.tfvars.example` | File mẫu để người dùng copy ra `terraform.tfvars` rồi điền giá trị thật. |
| `terraform/terraform.tfvars` | File chứa giá trị cấu hình thật cho Terraform. File này thường có thông tin môi trường riêng nên không nên public. |
| `terraform/main.tf` | File chính tạo hạ tầng AWS: VPC, Internet Gateway, subnet public, route table, security group, 3 EC2 và file inventory cho Ansible. |
| `terraform/outputs.tf` | Khai báo các giá trị output sau khi `terraform apply`, ví dụ IP master, IP worker và đường dẫn inventory Ansible. |
| `terraform/.terraform.lock.hcl` | File khóa phiên bản provider để các lần chạy Terraform dùng đúng provider đã được chọn. |
| `terraform/terraform.tfstate` | File state lưu trạng thái tài nguyên AWS mà Terraform đang quản lý. Đây là file quan trọng, không nên sửa tay. |
| `terraform/terraform.tfstate.backup` | Bản sao lưu gần nhất của file state Terraform. |
| `terraform/templates/inventory.ini.tftpl` | Template tạo file `ansible/inventory/aws.ini` từ IP của các EC2 sau khi Terraform chạy xong. |
| `terraform/.terraform/` | Thư mục cache do `terraform init` tạo ra, chứa provider đã tải về. Không cần sửa trực tiếp. |

## 3. Thư mục `ansible`

Thư mục `ansible` dùng để SSH vào các VPS, chuẩn bị máy chủ, cài k3s master, join worker và deploy manifest Kubernetes.

| File | Dùng để làm gì |
| --- | --- |
| `ansible/README.md` | Ghi chú ngắn về luồng deploy bằng Terraform và Ansible. |
| `ansible/ansible.cfg` | Cấu hình mặc định cho Ansible, ví dụ inventory, user SSH hoặc cách Ansible kết nối host. |
| `ansible/site.yml` | Playbook tổng, import lần lượt các playbook con: chuẩn bị VPS, cài master, cài worker và deploy app. |
| `ansible/kubeconfig` | File cấu hình để `kubectl` kết nối tới Kubernetes cluster k3s. File này được lấy từ master sau khi cài k3s. |
| `ansible/group_vars/all.yml` | Biến dùng chung cho toàn bộ playbook Ansible, ví dụ user SSH, đường dẫn kubeconfig, cấu hình k3s hoặc đường dẫn manifest. |
| `ansible/inventory/aws.ini.example` | File inventory mẫu, cho biết format khai báo master và worker. |
| `ansible/inventory/aws.ini` | File inventory thật chứa IP các VPS AWS. Thường được Terraform tự sinh từ template. |
| `ansible/playbooks/00-prepare.yml` | Chuẩn bị tất cả VPS trước khi cài k3s: cập nhật package, cài gói cần thiết và cấu hình hệ thống. |
| `ansible/playbooks/01-install-k3s-master.yml` | Cài k3s server trên node master, lấy token join cluster và lấy file kubeconfig. |
| `ansible/playbooks/02-install-k3s-workers.yml` | Cài k3s agent trên các node worker và join chúng vào master. |
| `ansible/playbooks/03-deploy-app.yml` | Deploy các file manifest Kubernetes của ứng dụng lên cluster. |

## 4. Thư mục `kubernetes`

Thư mục `kubernetes` chứa các manifest YAML để tạo namespace, cấu hình, secret, storage, database, backend, frontend, ingress, HTTPS và monitoring trong cluster.

| File | Dùng để làm gì |
| --- | --- |
| `kubernetes/00-namespace.yaml` | Tạo namespace cho ứng dụng `web-bansach` và namespace `monitoring`. |
| `kubernetes/01-configmap.yaml` | Lưu các cấu hình không nhạy cảm cho ứng dụng, ví dụ tên database, host Redis, host RabbitMQ, domain và CORS. |
| `kubernetes/02-secret.yaml` | Lưu các thông tin nhạy cảm như mật khẩu MySQL, Redis, RabbitMQ, JWT secret, Gmail, Cloudinary hoặc API key. Không nên public file này. |
| `kubernetes/03-storage.yaml` | Tạo PersistentVolumeClaim để lưu dữ liệu bền vững cho MySQL, Redis và RabbitMQ. |
| `kubernetes/04-datastores.yaml` | Deploy MySQL, Redis, RabbitMQ và các Service nội bộ tương ứng để backend kết nối. |
| `kubernetes/05-backend.yaml` | Deploy backend Spring Boot, khai báo biến môi trường, health check, Service và cấu hình Prometheus scrape metrics. |
| `kubernetes/06-frontend.yaml` | Deploy frontend React/Nginx và Service để expose frontend trong cluster. |
| `kubernetes/07-ingress.yaml` | Cấu hình Ingress/Traefik để truy cập frontend và backend qua domain, ví dụ domain chính và subdomain API. |
| `kubernetes/08-monitoring.yaml` | Deploy Prometheus, Grafana, node-exporter, mysqld-exporter và các quyền RBAC để thu thập metrics. |
| `kubernetes/09-cert-manager-issuer.yaml` | Tạo ClusterIssuer cho cert-manager để xin chứng chỉ SSL từ Let's Encrypt. |

## 5. Thư mục `monitoring`

Thư mục `monitoring` chứa cấu hình phụ cho Prometheus và Grafana, dùng khi chạy monitoring local hoặc provisioning dashboard/datasource.

| File | Dùng để làm gì |
| --- | --- |
| `monitoring/prometheus/prometheus.local.yml` | File cấu hình Prometheus dùng cho môi trường local hoặc kiểm thử scrape metrics. |
| `monitoring/grafana/provisioning/datasources/datasource.yml` | Cấu hình datasource để Grafana tự kết nối tới Prometheus. |
| `monitoring/grafana/provisioning/dashboards/dashboards.yml` | Cấu hình để Grafana tự load dashboard từ thư mục provisioning. |
| `monitoring/grafana/provisioning/dashboards/web-bansach-overview.json` | Dashboard Grafana tổng quan cho dự án Web Bán Sách, hiển thị metrics hệ thống, backend hoặc database. |

## 6. Thư mục `toolbox`

Thư mục `toolbox` dùng để build container chứa các công cụ DevOps cần thiết, giúp chạy deploy giống nhau trên mọi máy.

| File | Dùng để làm gì |
| --- | --- |
| `toolbox/Dockerfile` | Dockerfile tạo image toolbox có Terraform, Ansible, kubectl, AWS CLI và Docker CLI để thao tác hạ tầng/deploy. |

## 7. Ghi chú về các file nhạy cảm

Các file sau có thể chứa IP server, secret, state hạ tầng hoặc thông tin môi trường thật:

- `terraform/terraform.tfvars`
- `terraform/terraform.tfstate`
- `terraform/terraform.tfstate.backup`
- `ansible/inventory/aws.ini`
- `ansible/kubeconfig`
- `kubernetes/02-secret.yaml`

Không nên commit hoặc public các file này nếu repository mở công khai.
