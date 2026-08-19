# WebBanSach infrastructure

This folder contains a 3 VPS AWS deployment for a k3s Kubernetes cluster:

- `vps1`: k3s server/control-plane
- `vps2`: k3s worker
- `vps3`: k3s worker

## 1. Create AWS EC2 nodes

```bash
cd infrastructure/terraform
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

Set `allowed_ssh_cidr` to your public IP, for example `203.0.113.10/32`.
Terraform creates the Ansible inventory at `infrastructure/ansible/inventory/aws.ini`.

## 2. Install k3s with Ansible

```bash
cd ../ansible
ansible-playbook site.yml
```

The kubeconfig is saved at `infrastructure/ansible/kubeconfig`.

## 3. Create Kubernetes secrets

Copy `infrastructure/kubernetes/02-secret.example.yaml` to `02-secret.yaml` and fill real values.
Do not commit `02-secret.yaml`.

## 4. Deploy app manually

```bash
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/00-namespace.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/01-configmap.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/02-secret.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/03-storage.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/04-datastores.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/08-monitoring.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/05-backend.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/06-frontend.yaml
kubectl --kubeconfig infrastructure/ansible/kubeconfig apply -f infrastructure/kubernetes/07-ingress.yaml
```

Grafana is exposed on NodePort `30300`.
