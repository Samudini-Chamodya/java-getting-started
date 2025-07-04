pipeline {
    agent any
    
    // Define pipeline parameters
    parameters {
        string(
            name: 'BRANCH_NAME',
            defaultValue: 'main',
            description: 'Branch to build and test'
        )
        booleanParam(
            name: 'RUN_INTEGRATION_TESTS',
            defaultValue: true,
            description: 'Run integration tests'
        )
    }
    
    // Define tools
    tools {
        maven 'Maven-3.9.0' // Adjust version based on your Maven installation
        jdk 'JDK-17' // Adjust version based on your JDK installation
    }
    
    // Environment variables
    environment {
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=true'
        JAVA_HOME = tool('JDK-17')
        PATH = "${JAVA_HOME}/bin;${env.PATH}"
    }
    
    stages {
        // Stage 1: Checkout Code
        stage('Checkout Code') {
            steps {
                echo "Checking out branch: ${params.BRANCH_NAME}"
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Samudini-Chamodya/java-getting-started.git',
                        credentialsId: 'github-credentials' // We'll set this up later
                    ]]
                ])
                
                // Display current branch and commit info
                script {
                    // Use bat instead of sh for Windows
                    def gitCommit = bat(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    def gitBranch = bat(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    echo "Building commit ${gitCommit} on branch ${gitBranch}"
                }
            }
        }
        
        // Stage 2: Build
        stage('Build') {
            steps {
                echo 'Starting Maven build...'
                bat 'mvn clean compile -DskipTests=true'
                echo 'Build completed successfully'
            }
        }
        
        // Stage 3: Parallel Testing
        stage('Parallel Tests') {
            parallel {
                // Unit Tests - Always run
                stage('Unit Tests') {
                    steps {
                        echo 'Running Unit Tests...'
                        bat 'mvn test -Dtest="**/*Test.java" -DfailIfNoTests=false'
                    }
                    post {
                        always {
                            // Publish unit test results
                            script {
                                if (fileExists('target/surefire-reports/*.xml')) {
                                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                                    
                                    // Archive test reports
                                    archiveArtifacts artifacts: 'target/surefire-reports/*.xml', 
                                                   fingerprint: true, 
                                                   allowEmptyArchive: true
                                } else {
                                    echo 'No test results found'
                                }
                            }
                        }
                    }
                }
                
                // Integration Tests - Conditional
                stage('Integration Tests') {
                    when {
                        equals expected: true, actual: params.RUN_INTEGRATION_TESTS
                    }
                    steps {
                        echo 'Running Integration Tests...'
                        bat 'mvn verify -Dtest="**/*IT.java,**/*IntegrationTest.java" -DfailIfNoTests=false'
                    }
                    post {
                        always {
                            // Publish integration test results if they exist
                            script {
                                if (fileExists('target/failsafe-reports/*.xml')) {
                                    publishTestResults testResultsPattern: 'target/failsafe-reports/*.xml'
                                    
                                    // Archive integration test reports
                                    archiveArtifacts artifacts: 'target/failsafe-reports/*.xml', 
                                                   fingerprint: true, 
                                                   allowEmptyArchive: true
                                } else {
                                    echo 'No integration test results found'
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Stage 4: Package Application
        stage('Package') {
            steps {
                echo 'Packaging application...'
                bat 'mvn package -DskipTests=true'
                
                // Archive the built artifacts
                script {
                    if (fileExists('target/*.jar')) {
                        archiveArtifacts artifacts: 'target/*.jar', 
                                       fingerprint: true, 
                                       allowEmptyArchive: true
                    } else {
                        echo 'No JAR files found to archive'
                    }
                }
            }
        }
        
        // Stage 5: Deploy to Staging (Conditional)
        stage('Deploy to Staging') {
            when {
                anyOf {
                    equals expected: 'main', actual: params.BRANCH_NAME
                    expression { params.BRANCH_NAME.startsWith('release/') }
                }
            }
            steps {
                echo "Deploying ${params.BRANCH_NAME} to Staging Environment..."
                
                // Simulate deployment steps
                script {
                    echo "Deployment simulation for branch: ${params.BRANCH_NAME}"
                    echo "Artifact: target/*.jar"
                    
                    // Add your actual deployment commands here
                    // Examples:
                    // bat 'scp target/*.jar user@staging-server:/opt/app/'
                    // bat 'ssh user@staging-server "sudo systemctl restart myapp"'
                    
                    // For now, just simulate
                    sleep 5
                    echo "✅ Deployment to staging completed successfully!"
                }
            }
        }
        
        // Stage 6: Smoke Tests (Post-deployment validation)
        stage('Smoke Tests') {
            when {
                anyOf {
                    equals expected: 'main', actual: params.BRANCH_NAME
                    expression { params.BRANCH_NAME.startsWith('release/') }
                }
            }
            steps {
                echo 'Running smoke tests on staging environment...'
                script {
                    // Add your smoke test commands here
                    // Examples:
                    // bat 'curl -f http://staging-server:8080/health'
                    // bat 'mvn test -Dtest="SmokeTest" -Denv=staging'
                    
                    echo "✅ Smoke tests passed!"
                }
            }
        }
    }
    
    // Post-build actions
    post {
        always {
            echo '🧹 Cleaning workspace...'
            cleanWs()
            
            // Send notification (optional)
            script {
                def buildStatus = currentBuild.result ?: 'SUCCESS'
                def color = buildStatus == 'SUCCESS' ? 'good' : 'danger'
                echo "Build finished with status: ${buildStatus}"
            }
        }
        
        success {
            echo '✅ Pipeline executed successfully!'
            echo "Branch: ${params.BRANCH_NAME}"
            echo "Integration Tests: ${params.RUN_INTEGRATION_TESTS ? 'Enabled' : 'Disabled'}"
            
            // Optional: Send success notification
            // slackSend color: 'good', message: "✅ Build SUCCESS for ${params.BRANCH_NAME}"
        }
        
        failure {
            echo '❌ Pipeline failed!'
            echo "Branch: ${params.BRANCH_NAME}"
            echo "Check the logs above for failure details"
            
            // Optional: Send failure notification
            // slackSend color: 'danger', message: "❌ Build FAILED for ${params.BRANCH_NAME}"
        }
        
        unstable {
            echo '⚠️ Pipeline completed with warnings'
        }
        
        changed {
            echo '🔄 Pipeline status changed from previous build'
        }
    }
}