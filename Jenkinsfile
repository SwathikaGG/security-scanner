pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
        OUTPUT_DIR = "${WORKSPACE}/output"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/SwathikaGG/security-scanner.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Run OWASP Dependency-Check') {
            steps {
                sh 'bash scripts/run-owasp.sh'
            }
        }

        stage('Run Python Safety Check') {
            steps {
                sh 'bash scripts/run-safety.sh'
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'output/*.json', fingerprint: true
            }
        }
    }

    post {
        always {
            echo "Pipeline completed!"
        }
    }
}
