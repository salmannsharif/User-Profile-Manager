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
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Stop and remove existing services
                    bat '''
                    docker-compose down || exit 0
                    '''
                    
                    // Start services with docker-compose
                    bat 'docker-compose up -d'
                    
                    // Wait for services to be healthy
                    sleep time: 30, unit: 'SECONDS'
                    
                    // Verify services are running
                    bat 'docker-compose ps'
                }
            }
        }
    }
    post {
        success {
            echo "Pipeline succeeded! Application and database running via Docker Compose on port 8081."
        }
        failure {
            echo "Pipeline failed. Check logs with: docker-compose logs"
            // Cleanup on failure
            bat 'docker-compose down || exit 0'
        }
        always {
            echo "Pipeline execution completed."
        }
    }
}
