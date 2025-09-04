pipeline {
    agent any

    tools {
        maven "M3"  // Make sure 'M3' is configured in Jenkins → Global Tools
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/salmannsharif/User-Profile-Manager.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                bat "mvn clean package -DskipTests"
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    echo '✅ JAR file archived successfully.'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying Application to port 8081...'

                // Step 1: Kill any existing process on port 8081
                bat '''
                echo Checking for existing process on port 8081...
                for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
                    echo Found process on port 8081 with PID=%%a
                    taskkill /PID %%a /F
                    if %errorlevel% equ 0 (
                        echo Successfully killed process %%a.
                    ) else (
                        echo Failed to kill process %%a.
                        exit /b 1
                    )
                )
                echo No running process found on port 8081 or already stopped.
                '''

                // Step 2: Start Spring Boot app in background with logging
                bat '''
                cd target
                echo Starting Spring Boot app in background...
                start /B java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081 > app.log 2>&1
                echo App started. Logs are available at target\\app.log
                '''

                // Step 3: Wait a few seconds and check log
                bat '''
                cd target
                timeout /t 5 > nul
                echo Last 10 lines of app.log:
                tail -n 10 app.log
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful! Application is running on http://localhost:8081"
            echo "💡 You can view logs at: C:\\Users\\CIPL1586\\.jenkins\\workspace\\demo-app-with-pipeline\\target\\app.log"
        }
        failure {
            echo "❌ Deployment failed. Check the console output for errors."
            script {
                if (fileExists('target/app.log')) {
                    echo "📋 Showing app.log content:"
                    echo readFile('target/app.log')
                }
            }
        }
        always {
            echo "Pipeline execution completed at ${new Date()}"
        }
    }
}
