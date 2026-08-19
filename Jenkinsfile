pipeline {
  agent any

  environment {
    REGISTRY = 'ghcr.io'
    IMAGE_PREFIX = 'ghcr.io/OWNER/REPO'
    BACKEND_IMAGE = "${IMAGE_PREFIX}-backend"
    FRONTEND_IMAGE = "${IMAGE_PREFIX}-frontend"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Test Backend') {
      steps {
        dir('Back_end') {
          script {
            if (isUnix()) {
              sh 'mvn test'
            } else {
              bat '.\\mvnw.cmd test'
            }
          }
        }
      }
    }

    stage('Test Frontend') {
      steps {
        dir('front_end') {
          script {
            if (isUnix()) {
              sh 'npm ci'
              sh 'npm test'
              sh 'npm run build'
            } else {
              bat 'npm ci'
              bat 'npm test'
              bat 'npm run build'
            }
          }
        }
      }
    }

    stage('Build Images') {
      steps {
        script {
          if (isUnix()) {
            sh 'docker build -t $BACKEND_IMAGE:$BUILD_NUMBER -t $BACKEND_IMAGE:latest Back_end'
            sh 'docker build --build-arg VITE_API_BASE_URL=$VITE_API_BASE_URL -t $FRONTEND_IMAGE:$BUILD_NUMBER -t $FRONTEND_IMAGE:latest front_end'
          } else {
            bat 'docker build -t %BACKEND_IMAGE%:%BUILD_NUMBER% -t %BACKEND_IMAGE%:latest Back_end'
            bat 'docker build --build-arg VITE_API_BASE_URL=%VITE_API_BASE_URL% -t %FRONTEND_IMAGE%:%BUILD_NUMBER% -t %FRONTEND_IMAGE%:latest front_end'
          }
        }
      }
    }

    stage('Push Images') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'ghcr-credentials', usernameVariable: 'GHCR_USER', passwordVariable: 'GHCR_TOKEN')]) {
          script {
            if (isUnix()) {
              sh 'echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin'
              sh 'docker push $BACKEND_IMAGE:$BUILD_NUMBER'
              sh 'docker push $BACKEND_IMAGE:latest'
              sh 'docker push $FRONTEND_IMAGE:$BUILD_NUMBER'
              sh 'docker push $FRONTEND_IMAGE:latest'
            } else {
              bat 'echo %GHCR_TOKEN% | docker login ghcr.io -u %GHCR_USER% --password-stdin'
              bat 'docker push %BACKEND_IMAGE%:%BUILD_NUMBER%'
              bat 'docker push %BACKEND_IMAGE%:latest'
              bat 'docker push %FRONTEND_IMAGE%:%BUILD_NUMBER%'
              bat 'docker push %FRONTEND_IMAGE%:latest'
            }
          }
        }
      }
    }

    stage('Deploy K3s') {
      steps {
        withCredentials([
          file(credentialsId: 'kubeconfig-k3s', variable: 'KUBECONFIG_FILE'),
          file(credentialsId: 'web-bansach-secret-yaml', variable: 'APP_SECRET_FILE')
        ]) {
          script {
            if (isUnix()) {
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/00-namespace.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/01-configmap.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f "$APP_SECRET_FILE"'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/03-storage.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/04-datastores.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/08-monitoring.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/05-backend.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/06-frontend.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" apply -f infrastructure/kubernetes/07-ingress.yaml'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" -n web-bansach set image deployment/backend backend=$BACKEND_IMAGE:$BUILD_NUMBER'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" -n web-bansach set image deployment/frontend frontend=$FRONTEND_IMAGE:$BUILD_NUMBER'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" -n web-bansach rollout status deployment/backend --timeout=180s'
              sh 'kubectl --kubeconfig="$KUBECONFIG_FILE" -n web-bansach rollout status deployment/frontend --timeout=180s'
            } else {
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/00-namespace.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/01-configmap.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f %APP_SECRET_FILE%'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/03-storage.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/04-datastores.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/08-monitoring.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/05-backend.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/06-frontend.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f infrastructure/kubernetes/07-ingress.yaml'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% -n web-bansach set image deployment/backend backend=%BACKEND_IMAGE%:%BUILD_NUMBER%'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% -n web-bansach set image deployment/frontend frontend=%FRONTEND_IMAGE%:%BUILD_NUMBER%'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% -n web-bansach rollout status deployment/backend --timeout=180s'
              bat 'kubectl --kubeconfig=%KUBECONFIG_FILE% -n web-bansach rollout status deployment/frontend --timeout=180s'
            }
          }
        }
      }
    }
  }
}
