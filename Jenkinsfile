pipeline {
    agent any

    tools {
        maven 'Maven-3.9.0'
        jdk 'JDK-17'
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to build and test')
        booleanParam(name: 'RUN_INTEGRATION_TESTS', defaultValue: true, description: 'Run integration tests')
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[url: 'https://github.com/Samudini-Chamodya/java-getting-started.git']]
                ])
                echo "Checking out branch: ${params.BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                echo 'Starting Maven build...'
                bat "mvn clean compile -DskipTests=true"
                echo 'Build completed successfully'
            }
        }

        stage('Parallel Tests') {
            parallel {
                stage('Unit Tests') {
                    when {
                        expression { return true }
                    }
                    steps {
                        echo 'Running Unit Tests...'
                        bat "mvn test -DfailIfNoTests=false"
                    }
                    post {
                        always {
                            junit '**/target/surefire-reports/*.xml'
                        }
                    }
                }

                stage('Integration Tests') {
                    when {
                        expression { return params.RUN_INTEGRATION_TESTS }
                    }
                    steps {
                        echo 'Running Integration Tests...'
                        bat "mvn verify -Pintegration-tests -DfailIfNoTests=false"
                    }
                    post {
                        always {
                            junit '**/target/failsafe-reports/*.xml'
                        }
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application...'
                bat "mvn package -DskipTests"
            }
        }

        stage('Deploy to Staging') {
            steps {
                echo 'Deploying to staging environment...'
                // Simulate deployment step
            }
        }

        stage('Smoke Tests') {
            steps {
                echo 'Running smoke tests on staging...'
                // Simulate smoke tests
            }
        }
    }

    post {
        always {
            echo "🧹 Cleaning workspace..."
            cleanWs()
            echo "Build finished with status: ${currentBuild.currentResult}"
        }
        failure {
            echo "❌ Pipeline failed!"
            echo "Branch: ${params.BRANCH_NAME}"
            echo "Check the logs above for failure details"
        }
        success {
            echo "✅ Pipeline succeeded!"
        }
    }
}
