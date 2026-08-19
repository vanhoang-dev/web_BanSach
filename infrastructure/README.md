# Lệnh Deploy Dự Án Web Bán Sách

File này là checklist lệnh deploy. Chạy theo thứ tự từ trên xuống.

## 1. Mở Docker Toolbox

Chạy trên PowerShell tại thư mục gốc dự án.

```powershell
docker compose -f docker-compose.tools.yml build
```

Build container chứa Terraform, Ansible, kubectl, AWS CLI, Docker CLI.

```powershell
docker compose -f docker-compose.tools.yml run --rm --service-ports devops-tools
```

Vào container toolbox.

```bash
terraform version
ansible --version
kubectl version --client
aws --version
docker version
```

Kiểm tra các tool đã cài.

## 2. Cấu Hình AWS CLI

```bash
export AWS_PAGER=""
```

Tắt pager AWS CLI.

```bash
aws configure
```

Nhập AWS key, region `ap-southeast-1`, output `json`.

```bash
aws sts get-caller-identity
```

Kiểm tra AWS CLI đã đăng nhập đúng.

## 3. Tạo SSH Key Pair

Chỉ chạy nếu chưa có key `web-bansach-key`.

```bash
mkdir -p /root/.ssh
```

Tạo thư mục chứa key.

```bash
aws ec2 create-key-pair \
  --region ap-southeast-1 \
  --key-name web-bansach-key \
  --query 'KeyMaterial' \
  --output text > /root/.ssh/web-bansach-key.pem
```

Tạo key pair trên AWS.

```bash
chmod 400 /root/.ssh/web-bansach-key.pem
```

Cấp quyền đúng cho private key.

```bash
aws ec2 describe-key-pairs \
  --region ap-southeast-1 \
  --key-names web-bansach-key
```

Kiểm tra key đã tồn tại.

## 4. Tạo 3 VPS Bằng Terraform

```bash
cp /workspace/infrastructure/terraform/terraform.tfvars.example /workspace/infrastructure/terraform/terraform.tfvars
```

Tạo file biến Terraform.

```bash
curl https://checkip.amazonaws.com
```

Lấy IP máy hiện tại để điền vào `terraform.tfvars`.

```bash
cd /workspace/infrastructure/terraform
```

Vào thư mục Terraform.

```bash
terraform init
```

Khởi tạo Terraform.

```bash
terraform plan
```

Xem tài nguyên sẽ tạo.

```bash
terraform apply
```

Tạo VPS, nhập `yes` để xác nhận.

```bash
terraform output
```

Xem IP VPS và inventory Ansible.

## 5. Cài k3s Bằng Ansible

```bash
cd /workspace/infrastructure/ansible
```

Vào thư mục Ansible.

```bash
ANSIBLE_HOST_KEY_CHECKING=False ansible -i inventory/aws.ini all -m ping
```

Kiểm tra SSH tới 3 VPS.

```bash
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -i inventory/aws.ini site.yml
```

Cài k3s và deploy manifest.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get nodes
```

Kiểm tra 3 node Kubernetes.

## 6. Apply Secret Kubernetes

Tạo file secret thật tại:

```text
/workspace/infrastructure/kubernetes/02-secret.yaml
```

Apply secret:

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  apply -f /workspace/infrastructure/kubernetes/02-secret.yaml
```

Tạo secret cho backend/database.

Restart các service:

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout restart deployment/mysql
```

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout restart deployment/redis
```

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout restart deployment/rabbitmq
```

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout restart deployment/backend
```

## 7. Build Và Push Docker Hub

```bash
docker login -u hoangdev311
```

Đăng nhập Docker Hub.

```bash
docker build -t hoangdev311/web-bansach-backend:latest /workspace/Back_end
```

Build backend image.

```bash
docker build \
  --build-arg VITE_API_BASE_URL=https://api.hoanghh.xyz \
  -t hoangdev311/web-bansach-frontend:latest \
  /workspace/front_end
```

Build frontend image.

```bash
docker push hoangdev311/web-bansach-backend:latest
```

Push backend lên Docker Hub.

```bash
docker push hoangdev311/web-bansach-frontend:latest
```

Push frontend lên Docker Hub.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach set image deployment/backend \
  backend=hoangdev311/web-bansach-backend:latest
```

Cập nhật image backend.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach set image deployment/frontend \
  frontend=hoangdev311/web-bansach-frontend:latest
```

Cập nhật image frontend.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout status deployment/backend --timeout=180s
```

Chờ backend deploy xong.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout status deployment/frontend --timeout=180s
```

Chờ frontend deploy xong.

## 8. Apply Kubernetes Manifest

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/00-namespace.yaml
```

Tạo namespace.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/01-configmap.yaml
```

Apply ConfigMap.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/03-storage.yaml
```

Tạo storage.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/04-datastores.yaml
```

Deploy MySQL, Redis, RabbitMQ.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/05-backend.yaml
```

Deploy backend.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/06-frontend.yaml
```

Deploy frontend.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/07-ingress.yaml
```

Apply Ingress.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/08-monitoring.yaml
```

Deploy monitoring.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/09-cert-manager-issuer.yaml
```

Apply HTTPS issuer.

## 9. HTTPS

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.15.3/cert-manager.yaml
```

Cài cert-manager.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -n cert-manager
```

Kiểm tra cert-manager.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/09-cert-manager-issuer.yaml
```

Apply issuer.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig apply -f /workspace/infrastructure/kubernetes/07-ingress.yaml
```

Apply ingress TLS.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get certificate -n web-bansach
```

Kiểm tra SSL.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig describe challenge -n web-bansach
```

Xem lỗi SSL nếu có.

## 10. Kiểm Tra Web

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -n web-bansach
```

Kiểm tra pod dự án.

```bash
curl https://api.hoanghh.xyz/actuator/health
```

Kiểm tra backend.

```text
https://hoanghh.xyz
```

Mở website.

## 11. Import data.sql

```bash
MYSQL_POD=$(kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach get pod -l app=mysql \
  -o jsonpath='{.items[0].metadata.name}')
```

Lấy pod MySQL.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach cp /workspace/Back_end/data.sql $MYSQL_POD:/tmp/data.sql
```

Copy data.sql vào pod.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach exec -it $MYSQL_POD -- sh -c \
  'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" DUAN_WEBBANSACH < /tmp/data.sql'
```

Import dữ liệu.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach exec -it $MYSQL_POD -- sh -c \
  'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "USE DUAN_WEBBANSACH; SELECT COUNT(*) AS books FROM books; SELECT COUNT(*) AS users FROM users;"'
```

Kiểm tra dữ liệu.

## 12. HeidiSQL

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach port-forward --address 0.0.0.0 svc/mysql 3307:3306
```

Mở MySQL ra local.

HeidiSQL:

```text
Host: 127.0.0.1
Port: 3307
User: root
Password: MYSQL_ROOT_PASSWORD
Database: DUAN_WEBBANSACH
```

## 13. Monitoring

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -n monitoring
```

Kiểm tra monitoring.

```text
http://13.251.26.43:30300
```

Mở Grafana.

```text
admin / admin123
```

Tài khoản Grafana.

```text
http://prometheus.monitoring.svc.cluster.local:9090
```

Prometheus datasource trong Grafana.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n monitoring port-forward --address 0.0.0.0 svc/prometheus 9090:9090
```

Mở Prometheus local.

## 14. SSH VPS

```bash
ssh -i /root/.ssh/web-bansach-key.pem ubuntu@13.251.26.43
```

SSH vào master.

```bash
sudo kubectl get nodes
```

Kiểm tra node trên VPS.

```bash
exit
```

Thoát VPS.

## 15. GitHub Actions

Tạo `KUBE_CONFIG_B64`:

```bash
base64 -w 0 /workspace/infrastructure/ansible/kubeconfig
```

Copy vào GitHub Secret.

Secrets cần có:

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

Push vào `main` để tự deploy:

```powershell
git add .
git commit -m "Update deployment configuration"
git push origin main
```

## 16. Debug Nhanh

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig get pods -A
```

Xem toàn bộ pod.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach logs deployment/backend --tail=100
```

Xem log backend.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach describe pod -l app=backend
```

Xem lỗi backend.

```bash
kubectl --kubeconfig /workspace/infrastructure/ansible/kubeconfig \
  -n web-bansach rollout restart deployment/backend
```

Restart backend.

## 17. Xóa Hạ Tầng

```bash
cd /workspace/infrastructure/terraform
terraform destroy
```

Xóa toàn bộ hạ tầng AWS do Terraform tạo.

