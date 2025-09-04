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

                // Kill any existing process on port 8081
                bat '''
                echo Checking for existing process on port 8081...
                for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
                    echo Found process on port 8081 with PID=%%a
                    taskkill /PID %%a /F
                )
                echo No running process found or already stopped.
                '''

                // Start app in background with logging
                bat '''
                cd target
                echo Starting Spring Boot app in background...
                start /B java -jar UserProfileManager-0.0.1-SNAPSHOT.jar --server.port=8081 > app.log 2>&1
                echo App started. Logs are in target\\app.log
                '''

                // Wait 5 seconds (safe way on Windows)
                bat 'ping 127.0.0.1 -n 6 > nul'

                // Show last 10 lines of log (Windows-safe)
                bat '''
                cd target
                echo.
                echo === Last 10 lines of app.log ===
                powershell -Command "Get-Content app.log | Select-Object -Last 10"
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful! Application is running on http://localhost:8081"
            echo "💡 Logs: C:\\Users\\CIPL1586\\.jenkins\\workspace\\demo-app-with-pipeline@2\\target\\app.log"
        }
        failure {
            echo "❌ Deployment failed. Checking logs..."
            script {
                if (fileExists('target/app.log')) {
                    echo "📋 Last 15 lines of app.log:"
                    powershell '''
                        $log = Get-Content target/app.log -ErrorAction SilentlyContinue
                        if ($log) {
                            $log | Select-Object -Last 15
                        } else {
                            echo "Log file is empty or not readable."
                        }
                    '''
                } else {
                    echo "Log file (app.log) does not exist yet."
                }
            }
        }
        always {
            echo "Pipeline execution completed at ${new Date()}"
        }
    }
}
