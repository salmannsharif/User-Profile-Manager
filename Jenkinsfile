pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo-link.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Stop any existing process running on port 8081
                    bat '''
                        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /F /PID %%a
                    '''

                    // Start the new Spring Boot app in background
                    bat 'start /B java -jar target/UserProfileManager-0.0.1-SNAPSHOT.jar'
                }
            }
        }
    }
}
