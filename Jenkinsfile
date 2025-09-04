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
            // Stop & remove old container if exists, ignore errors
            bat '''
            docker stop userprofilemanager || exit 0
            docker rm userprofilemanager || exit 0
            '''

            // Run new container
            bat 'docker run -d -p 8081:8081 --name userprofilemanager userprofilemanager:latest'
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
