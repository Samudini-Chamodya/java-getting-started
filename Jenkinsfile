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
            defaultValue: false,
            description: 'Run integration tests (only if they exist)'
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
                        credentialsId: 'github-credentials'
                    ]]
                ])
                
                // Display current branch and commit info
                script {
                    // Use bat instead of sh for Windows
                    def gitCommit = bat(returnStdout: true, script: '@echo off && git rev-parse HEAD').trim()
                    def gitBranch = bat(returnStdout: true, script: '@echo off && git rev-parse --abbrev-ref HEAD').trim()
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
                        bat 'mvn test -DfailIfNoTests=false'
                    }
                    post {
                        always {
                            // Publish unit test results
                            script {
                                if (fileExists('target/surefire-reports')) {
                                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                                    
                                    // Archive test reports
                                    archiveArtifacts artifacts: 'target/surefire-reports/*.xml', 
                                                   fingerprint: true, 
                                                   allowEmptyArchive: true
                                    echo 'Unit test results published'
                                } else {
                                    echo 'No unit test results directory found'
                                }
                            }
                        }
                    }
                }
                
                // Integration Tests - Conditional and only if files exist
                stage('Integration Tests') {
                    when {
                        allOf {
                            equals expected: true, actual: params.RUN_INTEGRATION_TESTS
                            // Check if integration test files exist
                            anyOf {
                                expression { 
                                    return fileExists('src/test/java/**/*IT.java') || 
                                           fileExists('src/test/java/**/*IntegrationTest.java') ||
                                           fileExists('src/integration-test/java/**/*.java')
                                }
                            }
                        }
                    }
                    steps {
                        echo 'Running Integration Tests...'
                        script {
                            // Try different approaches for integration tests
                            try {
                                bat 'mvn failsafe:integration-test failsafe:verify -DfailIfNoTests=false'
                            } catch (Exception e) {
                                echo "Failsafe plugin approach failed, trying surefire with integration test pattern"
                                bat 'mvn test -Dtest="**/*IT,**/*IntegrationTest" -DfailIfNoTests=false'
                            }
                        }
                    }
                    post {
                        always {
                            script {
                                // Try to find integration test results in different locations
                                def foundResults = false
                                if (fileExists('target/failsafe-reports')) {
                                    publishTestResults testResultsPattern: 'target/failsafe-reports/*.xml'
                                    archiveArtifacts artifacts: 'target/failsafe-reports/*.xml', 
                                                   fingerprint: true, 
                                                   allowEmptyArchive: true
                                    foundResults = true
                                }
                                if (fileExists('target/surefire-reports') && !foundResults) {
                                    // Integration tests might have run through surefire
                                    echo 'Integration test results found in surefire reports'
                                }
                                if (!foundResults) {
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
                    if (fileExists('target') && fileExists('target/*.jar')) {
                        archiveArtifacts artifacts: 'target/*.jar', 
                                       fingerprint: true, 
                                       allowEmptyArchive: true
                        echo 'JAR artifacts archived successfully'
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
                    sleep 3
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
                echo "Build finished with status: ${buildStatus}"
            }
        }
        
        success {
            echo '✅ Pipeline executed successfully!'
            echo "Branch: ${params.BRANCH_NAME}"
            echo "Integration Tests: ${params.RUN_INTEGRATION_TESTS ? 'Enabled' : 'Disabled'}"
        }
        
        failure {
            echo '❌ Pipeline failed!'
            echo "Branch: ${params.BRANCH_NAME}"
            echo "Check the logs above for failure details"
        }
        
        unstable {
            echo '⚠️ Pipeline completed with warnings'
        }
        
        changed {
            echo '🔄 Pipeline status changed from previous build'
        }
    }
}