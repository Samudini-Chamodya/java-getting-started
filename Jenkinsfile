pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to build and test')
        booleanParam(name: 'RUN_INTEGRATION_TESTS', defaultValue: true, description: 'Run integration tests?')
    }

    environment {
        MAVEN_HOME = tool 'Maven-3.9.0' 
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[url: 'https://github.com/Samudini-Chamodya/java-getting-started.git']]
                ])
            }
        }

        stage('Build') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean install"
            }
        }

        stage('Tests') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh "${MAVEN_HOME}/bin/mvn test"
                    }
                }
                stage('Integration Tests') {
                    when {
                        expression { return params.RUN_INTEGRATION_TESTS }
                    }
                    steps {
                        sh "${MAVEN_HOME}/bin/mvn verify -Pintegration"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'main'
                    branch pattern: "release/.*", comparator: "REGEXP"
                }
            }
            steps {
                echo "Deploying to staging environment..."
              
                 script {
                    echo "Deployment simulation for branch: ${params.BRANCH_NAME}"
                    echo "Artifact: target/*.jar"
                    
                
                    sleep 5
                    echo "✅ Deployment to staging completed successfully!"
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo '✅ Build and tests succeeded!'
        }
        failure {
            echo '❌ Build or tests failed!'
        }
    }
}
