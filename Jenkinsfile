pipeline {
    agent any

    tools {
        maven "M3"  // Make sure 'M3' is configured in Jenkins Global Tools
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
        echo 'Deploying Application to port 8081...'
        // Kill existing process
        bat '''
        echo Checking for existing process on port 8081...
        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
            echo Found process on port 8081 with PID=%%a
            taskkill /PID %%a /F
        )
        '''
        // Start in background
        bat '''
        cd target
        echo Starting Spring Boot Application in background...
        start /B java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081
        timeout /t 10
        echo Application started in background
        '''
    }
}


    }

    post {
        success {
            echo "✅ Pipeline succeeded! Application should be running on port 8081."
        }
        failure {
            echo "❌ Pipeline failed. Check logs above for details."
        }
        always {
            echo "Pipeline execution completed."
        }
    }
}
