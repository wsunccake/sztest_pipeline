def szIP

node {
    properties([
            parameters([
                    string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: ''),
                    string(name: 'SCENARIO', defaultValue: '', description: ''),
                    string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: ''),
            ])
    ])

    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"


    stage('Setup SZ IP') {
        script {
            File szInp = new File("${VAR_DIR}/input/sz/sz.inp")
            szIP = szInp.readLines().get(0).split()[1]
            println "SZ Name: ${szInp.readLines().get(0).split()[0]}"
            println "SZ IP: ${szIP}"
        }
    }

    stage('Delete Entry On Local License Server') {
        build job: 'prepare_del_entry_on_lls',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'SZ_IP', value: "${szIP}"),
                ]
    }

    stage('Shutdown SZ') {
        build job: 'prepare_shutdown_sz',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                ]
    }

}
