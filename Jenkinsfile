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

        // Kill process on port 8081 if running (ignore if nothing found)
        bat '''
        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /PID %%a /F
        if errorlevel 1 (
            echo No process found on port 8081
        )
        '''

        // Start the Spring Boot app
        bat '''
        cd target
        start java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081
        '''
    }
}


    }
}
