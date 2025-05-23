pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
        OUTPUT_DIR = "${WORKSPACE}/output"
        DEP_CHECK_PATH = "/opt/dependency-check-12.1.0/dependency-check/bin/dependency-check.sh"
        NVD_API_KEY = credentials('NVD_API_KEY')
    }

    tools {
        maven 'Maven 3.8.7' // Name of the Maven tool configured in Jenkins
        jdk 'JDK-21'         // Name of the JDK tool configured in Jenkins
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
                    def outputDir = 'output'
                    if (!fileExists(outputDir)) {
                        sh "mkdir -p ${outputDir}"
                    } else {
                        echo "Output directory already exists."
                    }
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
                    sh '''
                        chmod +x scripts/run-owasp.sh
                        bash scripts/run-owasp.sh ${DEP_CHECK_PATH} ${NVD_API_KEY}
                    '''
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
                    def dbUser = "springbootuser" // match spring.datasource.username
                    def dbPass = "password" // match spring.datasource.password
                    def dbName = "security_scanner"
                    def json = sh(script: "cat ~/security-scanner/output/dependency-check-report.json", returnStdout: true).trim()
                    json = json.replaceAll('"', '\\"') // escape double quotes

                    sh """
                        mysql -u ${dbUser} -p${dbPass} -h localhost -D ${dbName} -e \\
                        "INSERT INTO scan_reports (report) VALUES (\\\"${json}\\\");"
                    """
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
