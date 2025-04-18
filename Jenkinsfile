pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
        OUTPUT_DIR = "${WORKSPACE}/output"
    }

    pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
        OUTPUT_DIR = "${WORKSPACE}/output"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/SwathikaGG/security-scanner.git'
            }
        }

        stage('Setup Output Directory') {
            steps {
                script {
                    // Create output directory if it doesn't exist
                    sh 'mkdir -p output'
                }
            }
        }

        stage('Build with Maven') {
            steps {
                echo '🔧 Building project with Maven...'
                sh 'mvn clean install'
            }
        }

        stage('Run OWASP Dependency-Check') {
            steps {
                echo '🛡️ Running OWASP Dependency-Check...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'chmod +x scripts/run-owasp.sh && bash scripts/run-owasp.sh'
                }
            }
        }

        // Uncomment this stage if you plan to run the Python Safety Check
        /*stage('Run Python Safety Check') {
            steps {
                echo '🐍 Running Python Safety Check...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'chmod +x scripts/run-safety.sh && bash scripts/run-safety.sh'
                }
            }
        }*/

        // Uncomment this stage to archive reports
        /*stage('Archive Reports') {
            steps {
                echo '🗃️ Archiving reports...'
                sh 'ls -lh $OUTPUT_DIR || echo "⚠️ Output directory not found!"'
                archiveArtifacts artifacts: 'output/*.json', fingerprint: true
            }
        }*/

        stage('Sync with MySQL') {
            steps {
                script {
                    sh 'mysql -u root -p admin -h localhost -e "INSERT INTO scan_reports (report) VALUES (\"$(cat ~/security-scanner/output/dependency-check-report.json)\");" security_scanner'
                }
            }
        }
    }

    post {
        always {
            echo "✅ Pipeline completed!"
        }
    }
}

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/SwathikaGG/security-scanner.git'
            }
        }

        stage('Setup Output Directory') {
            steps {
                script {
                    // Create output directory if it doesn't exist
                    sh 'mkdir -p output'
                }
            }
        }

        stage('Build with Maven') {
            steps {
                echo '🔧 Building project with Maven...'
                sh 'mvn clean install'
            }
        }

        stage('Run OWASP Dependency-Check') {
            steps {
                echo '🛡️ Running OWASP Dependency-Check...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'chmod +x scripts/run-owasp.sh && bash scripts/run-owasp.sh'
                }
            }
        }

        /*stage('Run Python Safety Check') {
            steps {
                echo '🐍 Running Python Safety Check...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'chmod +x scripts/run-safety.sh && bash scripts/run-safety.sh'
                }
            }
        }*/

        /*stage('Archive Reports') {
            steps {
                echo '🗃️ Archiving reports...'
                sh 'ls -lh $OUTPUT_DIR || echo "⚠️ Output directory not found!"'
                archiveArtifacts artifacts: 'output/*.json', fingerprint: true
            }
        }
    }*/
        stage('Sync with MySQL') {
            steps {
                script {
                    sh 'mysql -u root -p admin -h localhost -e "INSERT INTO scan_reports (report) VALUES (\"$(cat ~/security-scanner/output/dependency-check-report.json)\");" security_scanner'
                }
            }
        }
    post {
        always {
            echo "✅ Pipeline completed!"
        }
    }
}
