# DevOps toolbox

Container nay dung de chay cac cong cu deploy ma khong can cai truc tiep tren Windows:

- Terraform
- Ansible
- kubectl
- AWS CLI
- Docker CLI
- SSH client

Docker Desktop van can chay tren may host. Container se ket noi Docker Engine cua host qua `/var/run/docker.sock`.

## Build va vao container

Chay tai thu muc goc project:

```powershell
docker compose -f docker-compose.tools.yml build
docker compose -f docker-compose.tools.yml run --rm devops-tools
```

## Kiem tra cong cu

Ben trong container:

```bash
terraform version
ansible --version
kubectl version --client
aws --version
docker version
```

## AWS credentials

Thu muc host `.aws` duoc mount vao `/root/.aws`.
Neu chua cau hinh AWS:

```bash
aws configure
aws sts get-caller-identity
```

## SSH key

Dat file key tai:

```text
C:\Users\dangv\.ssh\web-bansach-key.pem
```

Ben trong container, duong dan la:

```text
/root/.ssh/web-bansach-key.pem
```

Trong `infrastructure/terraform/templates/inventory.ini.tftpl`, dung:

```ini
ansible_ssh_private_key_file=/root/.ssh/web-bansach-key.pem
```

## Chay Terraform va Ansible

```bash
cd infrastructure/terraform
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply

cd ../ansible
ansible all -m ping
ansible-playbook site.yml
kubectl --kubeconfig kubeconfig get nodes
```
