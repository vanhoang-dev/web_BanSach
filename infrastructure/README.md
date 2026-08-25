# Hướng Dẫn Chạy Server Và Deploy Dự Án

Tài liệu này là checklist lệnh deploy cho dự án Web Bán Sách. Các lệnh được chia theo từng giai đoạn để tránh nhầm giữa tạo server, deploy lần đầu, cập nhật code và kiểm tra hệ thống.

## Mục Lục

1. Chuẩn bị toolbox
2. Cấu hình AWS
3. Tạo VPS bằng Terraform
4. Cài Kubernetes k3s bằng Ansible
5. Deploy ứng dụng lần đầu
6. Deploy lại khi sửa code
7. Cấu hình HTTPS
8. Kiểm tra website
9. Import dữ liệu vào database
10. Kết nối database bằng HeidiSQL
11. Kiểm tra Grafana và Prometheus
12. SSH vào VPS
13. GitHub Actions CI/CD
14. Debug nhanh
15. Xóa hạ tầng

## 1. Chuẩn Bị Toolbox

Chạy trên PowerShell tại thư mục gốc dự án.

### 1.1. Build toolbox

```powershell
# Build container chứa Terraform, Ansible, kubectl, AWS CLI và Docker CLI
docker compose -f docker-compose.tools.yml build
```

### 1.2. Vào toolbox

```powershell
# Mở container toolbox để chạy các lệnh deploy
docker compose -f docker-compose.tools.yml run --rm --service-ports devops-tools
```

### 1.3. Kiểm tra công cụ trong toolbox

```bash
# Kiểm tra Terraform
terraform version

# Kiểm tra Ansible
ansible --version

# Kiểm tra kubectl
kubectl version --client

# Kiểm tra AWS CLI
aws --version

# Kiểm tra Docker CLI có kết nối được Docker Desktop không
docker version
```

## 2. Cấu Hình AWS

Chạy trong container toolbox.

### 2.1. Tắt AWS pager

```bash
# Tránh lỗi AWS CLI thiếu chương trình less
export AWS_PAGER=""
```

### 2.2. Đăng nhập AWS CLI

```bash
# Cấu hình Access Key, Secret Key, region và output
aws configure
```

Nhập:

```text
AWS Access Key ID: key của IAM user
AWS Secret Access Key: secret của IAM user
Default region name: ap-southeast-1
Default output format: json
```

### 2.3. Kiểm tra tài khoản AWS

```bash
# Xem AWS CLI đang dùng IAM user/account nào
aws sts get-caller-identity
```

### 2.4. Tạo SSH key pair nếu chưa có

```bash
# Tạo thư mục lưu private key
mkdir -p /root/.ssh
```

```bash
# Tạo key pair trên AWS và lưu private key vào toolbox
aws ec2 create-key-pair \
  --region ap-southeast-1 \
  --key-name web-bansach-key \
  --query 'KeyMaterial' \
  --output text > /root/.ssh/web-bansach-key.pem
```

```bash
# Cấp quyền an toàn cho private key để SSH chấp nhận
chmod 400 /root/.ssh/web-bansach-key.pem
```

```bash
# Kiểm tra key pair đã tồn tại trên AWS
aws ec2 describe-key-pairs \
  --region ap-southeast-1 \
  --key-names web-bansach-key
```

## 3. Tạo VPS Bằng Terraform

Chỉ chạy phần này khi chưa có VPS hoặc muốn tạo lại hạ tầng.

### 3.1. Tạo file biến Terraform

```bash
# Copy file mẫu thành file cấu hình thật
cp /workspace/infrastructure/terraform/terraform.tfvars.example /workspace/infrastructure/terraform/terraform.tfvars
```

### 3.2. Lấy IP máy hiện tại

```bash
# Lấy IP public của máy bạn để cấu hình admin_cidr
curl https://checkip.amazonaws.com
```

Sau đó mở `infrastructure/terraform/terraform.tfvars` và điền IP vào biến `admin_cidr`.

### 3.3. Chạy Terraform

```bash
# Vào thư mục chứa Terraform
cd /workspace/infrastructure/terraform
```

```bash
# Tải provider và khởi tạo Terraform
terraform init
```

```bash
# Xem trước các tài nguyên AWS sẽ được tạo
terraform plan
```

```bash
# Tạo VPC, Security Group và 3 VPS EC2
terraform apply
```

Khi Terraform hỏi xác nhận, nhập:

```text
yes
```

```bash
# Xem IP VPS master/worker và đường dẫn inventory Ansible
terraform output
```

## 4. Cài Kubernetes k3s Bằng Ansible

Chỉ chạy phần này sau khi Terraform đã tạo VPS.

### 4.1. Vào thư mục Ansible

```bash
# Chuyển tới thư mục chứa playbook Ansible
cd /workspace/infrastructure/ansible
```

### 4.2. Kiểm tra SSH tới VPS

```bash
# Kiểm tra Ansible có SSH được tới vps1, vps2, vps3 không
ANSIBLE_HOST_KEY_CHECKING=False ansible -i inventory/aws.ini all -m ping
```

### 4.3. Cài k3s và deploy manifest ban đầu

```bash
# Cài k3s master, join worker và apply manifest Kubernetes
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -i inventory/aws.ini site.yml
```

### 4.4. Kiểm tra node Kubernetes

```bash
# Kiểm tra 3 VPS đã vào cluster và ở trạng thái Ready chưa
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get nodes
```

## 5. Deploy Ứng Dụng Lần Đầu

Dùng khi cluster đã có nhưng ứng dụng chưa chạy hoặc muốn apply lại toàn bộ manifest.

### 5.1. Apply namespace

```bash
# Tạo namespace web-bansach và monitoring
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/00-namespace.yaml
```

### 5.2. Apply ConfigMap

```bash
# Tạo cấu hình chung cho backend: database URL, Redis, RabbitMQ, domain, CORS
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/01-configmap.yaml
```

### 5.3. Apply Secret

```bash
# Tạo secret chứa password/token/API key cho backend và database
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/02-secret.yaml
```

### 5.4. Apply storage

```bash
# Tạo volume lưu dữ liệu MySQL, Redis, RabbitMQ
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/03-storage.yaml
```

### 5.5. Deploy database/cache/message broker

```bash
# Deploy MySQL, Redis và RabbitMQ
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/04-datastores.yaml
```

### 5.6. Deploy backend

```bash
# Deploy backend Spring Boot
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/05-backend.yaml
```

### 5.7. Deploy frontend

```bash
# Deploy frontend React/Nginx
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/06-frontend.yaml
```

### 5.8. Apply ingress

```bash
# Cấu hình domain hoanghh.xyz và api.hoanghh.xyz qua Traefik
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/07-ingress.yaml
```

### 5.9. Deploy monitoring

```bash
# Deploy Prometheus, Grafana, node-exporter và mysqld-exporter
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/08-monitoring.yaml
```

### 5.10. Kiểm tra pod

```bash
# Kiểm tra pod backend, frontend, MySQL, Redis, RabbitMQ
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -n web-bansach
```

## 6. Deploy Lại Khi Sửa Code

Dùng phần này khi server đã chạy và chỉ muốn cập nhật code mới.

### 6.1. Đăng nhập Docker Hub

```bash
# Đăng nhập Docker Hub để push image
docker login -u hoangdev311
```

### 6.2. Build backend image

```bash
# Build image backend từ thư mục Back_end
docker build -t hoangdev311/web-bansach-backend:latest /workspace/Back_end
```

### 6.3. Build frontend image

```bash
# Build image frontend và nhúng API URL production
docker build \
  --build-arg VITE_API_BASE_URL=https://api.hoanghh.xyz \
  -t hoangdev311/web-bansach-frontend:latest \
  /workspace/front_end
```

### 6.4. Push image lên Docker Hub

```bash
# Push backend image lên Docker Hub
docker push hoangdev311/web-bansach-backend:latest
```

```bash
# Push frontend image lên Docker Hub
docker push hoangdev311/web-bansach-frontend:latest
```

### 6.5. Cập nhật image trong Kubernetes

```bash
# Cho backend deployment dùng image mới
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach set image deployment/backend \
  backend=hoangdev311/web-bansach-backend:latest
```

```bash
# Cho frontend deployment dùng image mới
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach set image deployment/frontend \
  frontend=hoangdev311/web-bansach-frontend:latest
```

### 6.6. Chờ rollout

```bash
# Chờ backend cập nhật xong
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout status deployment/backend --timeout=180s
```

```bash
# Chờ frontend cập nhật xong
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout status deployment/frontend --timeout=180s
```

## 7. Cấu Hình HTTPS

Chỉ chạy khi chưa cài HTTPS hoặc cần apply lại certificate.

### 7.1. Cài cert-manager

```bash
# Cài cert-manager để tự động xin SSL Let's Encrypt
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.15.3/cert-manager.yaml
```

### 7.2. Kiểm tra cert-manager

```bash
# Kiểm tra cert-manager đã chạy chưa
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -n cert-manager
```

### 7.3. Apply issuer

```bash
# Tạo ClusterIssuer dùng Let's Encrypt
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/09-cert-manager-issuer.yaml
```

### 7.4. Apply ingress TLS

```bash
# Apply Ingress có khai báo TLS
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/07-ingress.yaml
```

### 7.5. Kiểm tra SSL

```bash
# Kiểm tra certificate đã Ready chưa
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get certificate -n web-bansach
```

### 7.6. Xem lỗi SSL

```bash
# Xem lỗi khi certificate chưa Ready
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig describe challenge -n web-bansach
```

## 8. Kiểm Tra Website

### 8.1. Kiểm tra pod

```bash
# Xem trạng thái pod của dự án
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -n web-bansach
```

### 8.2. Kiểm tra backend

```bash
# Kiểm tra backend health
curl https://api.hoanghh.xyz/actuator/health
```

Backend ổn khi trả:

```json
{"status":"UP"}
```

### 8.3. Mở frontend

```text
https://hoanghh.xyz
```

Mở website production.

## 9. Import Dữ Liệu Vào Database

Dùng khi cần nạp dữ liệu mẫu từ `Back_end/data.sql`.

### 9.1. Lấy pod MySQL

```bash
# Lưu tên pod MySQL vào biến MYSQL_POD
MYSQL_POD=$(kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach get pod -l app=mysql \
  -o jsonpath='{.items[0].metadata.name}')
```

### 9.2. Copy file data.sql

```bash
# Copy file data.sql từ source code vào pod MySQL
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach cp /workspace/Back_end/data.sql $MYSQL_POD:/tmp/data.sql
```

### 9.3. Import data

```bash
# Import dữ liệu vào database DUAN_WEBBANSACH
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach exec -it $MYSQL_POD -- sh -c \
  'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" DUAN_WEBBANSACH < /tmp/data.sql'
```

### 9.4. Kiểm tra dữ liệu

```bash
# Đếm số sách và user sau khi import
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach exec -it $MYSQL_POD -- sh -c \
  'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "USE DUAN_WEBBANSACH; SELECT COUNT(*) AS books FROM books; SELECT COUNT(*) AS users FROM users;"'
```

## 10. Kết Nối Database Bằng HeidiSQL

### 10.1. Mở port-forward

```bash
# Mở MySQL trong Kubernetes ra máy local tại port 3307
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach port-forward --address 0.0.0.0 svc/mysql 3307:3306
```

### 10.2. Cấu hình HeidiSQL

```text
Host: 127.0.0.1
Port: 3307
User: root
Password: MYSQL_ROOT_PASSWORD
Database: DUAN_WEBBANSACH
```

## 11. Kiểm Tra Grafana Và Prometheus

### 11.1. Kiểm tra pod monitoring

```bash
# Xem Grafana, Prometheus và exporter đã chạy chưa
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -n monitoring
```

### 11.2. Mở Grafana

```text
http://13.251.26.43:30300
```

Tài khoản:

```text
admin / admin123
```

### 11.3. Thêm Prometheus datasource trong Grafana

```text
http://prometheus.monitoring.svc.cluster.local:9090
```

URL nội bộ để Grafana đọc dữ liệu từ Prometheus.

### 11.4. Mở Prometheus bằng port-forward

```bash
# Forward Prometheus ra local port 9090
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n monitoring port-forward --address 0.0.0.0 svc/prometheus 9090:9090
```

Sau đó mở:

```text
http://127.0.0.1:9090
```

## 12. SSH Vào VPS

### 12.1. SSH vào master

```bash
# Đăng nhập VPS master
ssh -i /root/.ssh/web-bansach-key.pem ubuntu@13.251.26.43
```

### 12.2. Kiểm tra node trên VPS

```bash
# Xem node Kubernetes trực tiếp trên master
sudo kubectl get nodes
```

### 12.3. Thoát VPS

```bash
# Thoát SSH
exit
```

## 13. GitHub Actions CI/CD

Khi đã cấu hình CI/CD, chỉ cần push hoặc merge vào `main`.

### 13.1. Tạo KUBE_CONFIG_B64

```bash
# Mã hóa kubeconfig để đưa vào GitHub Secret
base64 -w 0 /workspace/infrastructure/ansible/kubeconfig
```

Copy output vào GitHub Secret `KUBE_CONFIG_B64`.

### 13.2. GitHub Secrets cần có

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN
KUBE_CONFIG_B64
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
MYSQL_ROOT_PASSWORD
REDIS_PASSWORD
RABBITMQ_PASSWORD
APP_JWT_SECRET
GMAIL_USERNAME
GMAIL_APP_PASSWORD
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
SEPAY_ACCOUNT_NUMBER
SEPAY_ACCOUNT_NAME
SEPAY_WEBHOOK_API_KEY
```

### 13.3. Push code để deploy

Nếu đang ở `main`:

```powershell
# Commit và push vào main để GitHub Actions tự deploy
git add .
git commit -m "Update deployment"
git push origin main
```

Nếu đang ở nhánh phụ:

```powershell
# Push nhánh phụ, sau đó tạo Pull Request vào main
git add .
git commit -m "Update deployment"
git push origin feature/Code_ux
```

## 14. Debug Nhanh

### 14.1. Xem toàn bộ pod

```bash
# Kiểm tra toàn bộ pod trong cluster
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -A
```

### 14.2. Xem log backend

```bash
# Xem 100 dòng log cuối của backend
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach logs deployment/backend --tail=100
```

### 14.3. Xem lỗi pod backend

```bash
# Xem event và cấu hình pod backend
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach describe pod -l app=backend
```

### 14.4. Restart backend

```bash
# Khởi động lại backend deployment
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout restart deployment/backend
```

### 14.5. Chờ backend rollout

```bash
# Chờ backend chạy lại xong
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout status deployment/backend --timeout=180s
```

## 15. Xóa Hạ Tầng

Chỉ chạy khi muốn xóa toàn bộ VPS AWS do Terraform tạo.

```bash
# Xóa EC2, network và security group do Terraform quản lý
cd /workspace/infrastructure/terraform
terraform destroy
```

Khi Terraform hỏi xác nhận, nhập:

```text
yes
```

