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
        
        // Kill existing process
        bat '''
        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /PID %%a /F
        '''
        
        // Start application in background with logging
        bat '''
        cd target
        start "UserProfileManager" cmd /c "java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081 > app.log 2>&1"
        '''
        
        // Wait and check if application started
        bat 'timeout /t 5 /nobreak'
        bat 'netstat -ano | findstr :8081 && echo Application started successfully || echo Application failed to start'
    }
       }
    }
}
