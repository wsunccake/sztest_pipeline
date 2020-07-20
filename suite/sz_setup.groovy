def szIP

node {
    properties([
            parameters([
                    string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: ''),
                    string(name: 'SCENARIO', defaultValue: 'phase1', description: ''),
                    string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: ''),
                    string(name: 'CLUSTER_NAME', defaultValue: '', description: ''),

                    string(name: 'SZ_NUM', defaultValue: '1', description: ''),

            ])
    ])

    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"

    stage('Launch SZ') {
        build job: 'prepare_launch_sz',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_NUM', value: "${SZ_NUM}"),
                ]
    }

    stage('Setup SZ IP') {
        script {
            File szInp = new File("${VAR_DIR}/input/sz/sz.inp")
            szIP = szInp.readLines().get(0).split()[1]
            println "SZ Name: ${szInp.readLines().get(0).split()[0]}"
            println "SZ IP: ${szIP}"
        }
    }

    stage('Fresh Install SZ') {
        build job: 'prepare_fresh_install',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${szIP}"),
                        string(name: 'CLUSTER_NAME', value: "${CLUSTER_NAME}"),
                ]
    }

    stage('Join SZ Cluster') {
        build job: 'prepare_join_cluster',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'CLUSTER_NAME', value: "${CLUSTER_NAME}"),
                ]
    }

}
