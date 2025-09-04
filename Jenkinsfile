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
        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
            echo Killing process on port 8081 with PID=%%a
            taskkill /PID %%a /F
        )
        '''

        // Start app and show logs (blocking)
        bat '''
        cd target
        echo Starting Spring Boot app...
        java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081
        '''
    }
}


    }
}
