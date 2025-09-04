pipeline {
    agent any

    environment {
        APP_NAME = "UserProfileManager"
        JAR_NAME = "UserProfileManager-0.0.1-SNAPSHOT.jar"
        PORT = "8081"
    }

    stages {
        stage('Build') {
            steps {
                echo "Building ${APP_NAME}..."
                bat "mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying ${APP_NAME} to port ${PORT}..."

                // Kill existing process on port 8081
                bat """
                echo Checking for existing process on port %PORT%...
                for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%PORT%') do (
                    echo Found process on port %PORT% with PID=%%a
                    taskkill /PID %%a /F
                )
                echo No running process found on port %PORT% or already stopped.
                """

                // Start Spring Boot app in background
                bat """
                cd target
                echo Starting Spring Boot Application on port %PORT%...
                start /B java -jar %JAR_NAME% --server.port=%PORT%
                """
            }
        }
    }
}
