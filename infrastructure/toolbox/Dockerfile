# Image Ubuntu dùng làm môi trường chạy tool DevOps
FROM ubuntu:24.04

# Phiên bản Terraform cần cài trong toolbox
ARG TERRAFORM_VERSION=1.9.8
# Phiên bản kubectl khớp với k3s cluster
ARG KUBECTL_VERSION=v1.30.6

# Không hỏi tương tác khi apt cài package
ENV DEBIAN_FRONTEND=noninteractive
# Múi giờ dùng trong container
ENV TZ=Asia/Ho_Chi_Minh

# Cài Ansible, Docker CLI, SSH client và các package nền
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
      ansible \
      ca-certificates \
      curl \
      docker.io \
      gnupg \
      openssh-client \
      python3 \
      python3-pip \
      tzdata \
      unzip \
    && ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
    && dpkg-reconfigure -f noninteractive tzdata \
    && rm -rf /var/lib/apt/lists/*

# Tải và cài Terraform
RUN curl -fsSL "https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip" -o /tmp/terraform.zip \
    && unzip -o /tmp/terraform.zip -d /usr/local/bin \
    && rm /tmp/terraform.zip

# Tải và cài kubectl
RUN curl -fsSL "https://dl.k8s.io/release/${KUBECTL_VERSION}/bin/linux/amd64/kubectl" -o /usr/local/bin/kubectl \
    && chmod +x /usr/local/bin/kubectl

# Tải và cài AWS CLI v2
RUN curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o /tmp/awscliv2.zip \
    && unzip -o /tmp/awscliv2.zip -d /tmp \
    && /tmp/aws/install \
    && rm -rf /tmp/aws /tmp/awscliv2.zip

# Thư mục mount source code dự án vào container
WORKDIR /workspace

# Mở bash để chạy Terraform, Ansible, kubectl, awscli
CMD ["/bin/bash"]
