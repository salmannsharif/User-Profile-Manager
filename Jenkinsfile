pipeline {
    agent any

    tools {
        maven "M3"   // Maven from Jenkins Global Tools
    }

    environment {
        DOCKER_IMAGE = "userprofilemanager:latest"  // image name
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/salmannsharif/User-Profile-Manager.git'
            }
        }

        stage('Build') {
            steps {
                bat "mvn clean package -DskipTests"
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    bat "docker build -t %DOCKER_IMAGE% ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // Stop old container if running
                    bat '''
                    docker ps -q --filter "name=userprofilemanager" | findstr . && docker stop userprofilemanager && docker rm userprofilemanager
                    '''

                    // Run new container
                    bat "docker run -d --name userprofilemanager -p 8081:8081 %DOCKER_IMAGE%"
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline succeeded! Application running inside Docker on port 8081."
        }
        failure {
            echo "❌ Pipeline failed. Check logs."
        }
        always {
            echo "Pipeline execution completed."
        }
    }
}
