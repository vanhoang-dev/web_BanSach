# AWS k3s deployment

1. Create AWS resources with Terraform from `infrastructure/terraform`.
2. Terraform writes `inventory/aws.ini`.
3. Create `infrastructure/kubernetes/02-secret.yaml` from `02-secret.example.yaml`.
4. Run:

```bash
ansible-playbook site.yml
```

The kubeconfig is written to `infrastructure/ansible/kubeconfig`.
