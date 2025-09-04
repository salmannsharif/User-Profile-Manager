pipeline {
    agent any

    tools {
        maven "M3"
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
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Deploying Application...'
                
                // Kill process on port 8081 if running
                bat '''
                for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /PID %%a /F
                '''
                
                // Start the Spring Boot app as a Windows service using NSSM (recommended)
                // Or use a method that survives pipeline completion
                bat '''
                cd target
                echo Starting application on port 8081...
                java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081 > app.log 2>&1 &
                '''
                
                // Wait a bit and check if app started
                bat 'timeout /t 10 /nobreak'
                bat 'netstat -ano | findstr :8081 || echo ERROR: Application not listening on port 8081'
            }
        }
    }
    
    post {
        always {
            // Display application logs for debugging
            bat 'type target\\app.log || echo No application logs found'
        }
    }
}
