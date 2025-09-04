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
            // Create Docker network if it doesn't exist
            bat '''
            docker network create app-network || exit 0
            '''
            
            // Stop & remove old containers if they exist
            bat '''
            docker stop userprofilemanager postgres-db || exit 0
            docker rm userprofilemanager postgres-db || exit 0
            '''
            
            // Start PostgreSQL container first
            bat '''
            docker run -d ^
                --name postgres-db ^
                --network app-network ^
                -e POSTGRES_DB=user_profile_db ^
                -e POSTGRES_USER=postgres ^
                -e POSTGRES_PASSWORD=root123 ^
                -p 5432:5432 ^
                postgres:13
            '''
            
            // Wait for PostgreSQL to be ready
            bat 'timeout /t 20'
            
            // Start application container
            bat '''
            docker run -d ^
                --name userprofilemanager ^
                --network app-network ^
                -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/user_profile_db ^
                -e SPRING_DATASOURCE_USERNAME=postgres ^
                -e SPRING_DATASOURCE_PASSWORD=root123 ^
                -p 8081:8081 ^
                userprofilemanager:latest
            '''
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
