def szIP

node {
    properties([
            parameters([
                    // common
                    string(name: 'SZ_VERSION', defaultValue: '5.2.1.1'),
                    string(name: 'SCENARIO', defaultValue: 'partner'),
                    string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: ''),

                    // prepare_copy_var_dir
                    string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/sztest/conf/${SCENARIO}', description: ''),

                    // suite_sz_setup
                    string(name: 'GCE_IMAGE', defaultValue: 'vscg-cloud-${SZ_VERSION}', description: ''),
                    string(name: 'SZ_NUM', defaultValue: '1', description: ''),

                    // end2end_join_associate
                    string(name: 'AP_VERSION', defaultValue: '5.2.1.1'),
                    string(name: 'AP_MODEL', defaultValue: 'R710', description: ''),
                    string(name: 'AP_NUM', defaultValue: '9999', description: ''),
                    string(name: 'UE_NUM', defaultValue: '90000', description: ''),
                    string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2.1-14-u1804.tar.xz  ', description: ''),

                    // query_api
                    string(name: 'NUM_CLIENT', defaultValue: '10', description: ''),
                    string(name: 'HATCH_RATE', defaultValue: '2', description: ''),
                    string(name: 'RUN_TIME', defaultValue: '20m1s', description: ''),
 
                    // stage control
                    string(name: 'is_skip_end2end', defaultValue: 'false', description: ''),
                    string(name: 'is_skip_query', defaultValue: 'false', description: ''),
                    string(name: 'is_skip_csv', defaultValue: 'false', description: ''),
                    string(name: 'is_skip_delete_config', defaultValue: 'false', description: ''),
                    string(name: 'is_clean_env', defaultValue: 'true', description: ''),

                    string(name: 'API_PERF_VER', defaultValue: 'v9_1', description: ''),
                    string(name: 'DATA_DIR', defaultValue: '/usr/share/nginx/html/api_perf/5.2.1.1/report/${SCENARIO}', description: ''),

           ])
    ])

    currentBuild.displayName = "${params.SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"

    stage('Prepare Var Dir') {
        build job: 'prepare_copy_var_dir',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'SRC_DIR', value: "${SRC_DIR}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                ],
                propagate: false
    }

    stage('Setup SZ') {
        build job: 'suite_sz_setup',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_NUM', value: "${SZ_NUM}"),
                        string(name: 'CLUSTER_NAME', value: "partner-${SCENARIO}"),
                        string(name: 'GCE_IMAGE', value: "${GCE_IMAGE}"),
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

    stage('Create Config') {
        build job: 'suite_partner_config_per_partner_domain',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${szIP}"),
                        string(name: 'NPROC', value: "8"),
                ]

        build job: 'suite_partner_config_per_zone',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${szIP}"),
                        string(name: 'NPROC', value: "8"),
                ]
    }

    stage('Join AP and Associate UE') {
        if (params.is_skip_end2end == "false") {
            build job: 'suite_end2end_join_and_associate',
                    parameters: [
                            string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                            string(name: 'SCENARIO', value: "${SCENARIO}"),
                            string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                            string(name: 'SZ_IP', value: "${szIP}"),

                            string(name: 'AP_VERSION', value: "${AP_VERSION}"),
                            string(name: 'AP_MODEL', value: "${AP_MODEL}"),
                            string(name: 'AP_NUM', value: "${AP_NUM}"),
                            string(name: 'UE_NUM', value: "${UE_NUM}"),
                            string(name: 'MADSZ_TGZ', value: "${MADSZ_TGZ}"),
                    ]
        } else {
            echo "Skip to Join AP and UE"
        }
    }

    stage('Test Query API') {
        if (params.is_skip_query == "false") {
            build job: 'partner_prepare_id_data',
                    parameters: [
                            string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                            string(name: 'SCENARIO', value: "${SCENARIO}"),
                            string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                            string(name: 'SZ_IP', value: "${szIP}"),
                    ]

            build job: 'test_run_locustio', parameters: [
                    string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                    string(name: 'SCENARIO', value: "${SCENARIO}"),
                    string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                    string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                    string(name: 'TASK_DIR', value: 'partner-query'),
                    string(name: 'SZ_IP', value: "${szIP}"),
                    string(name: 'NUM_CLIENT', value: "${NUM_CLIENT}"),
                    string(name: 'HATCH_RATE', value: "${HATCH_RATE}"),
                    string(name: 'RUN_TIME', value: "${RUN_TIME}"),
            ]

            build job: 'test_run_locustio', parameters: [
                    string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                    string(name: 'SCENARIO', value: "${SCENARIO}"),
                    string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                    string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                    string(name: 'TASK_DIR', value: 'partner-update'),
                    string(name: 'SZ_IP', value: "${szIP}"),
                    string(name: 'NUM_CLIENT', value: "${NUM_CLIENT}"),
                    string(name: 'HATCH_RATE', value: "${HATCH_RATE}"),
                    string(name: 'RUN_TIME', value: "${RUN_TIME}"),
            ]
        } else {
            echo "Skip to Test Query API"
        }
    }

    stage('Delete Config') {
        if (params.is_skip_delete_config == "false") {
            build job: 'partner_delete_wlan_per_zone',
                    parameters: [
                            string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                            string(name: 'SCENARIO', value: "${SCENARIO}"),
                            string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                            string(name: 'SZ_IP', value: "${szIP}"),
                            string(name: 'NPROC', value: "16"),
                    ],
                    propagate: false

            build job: 'suite_partner_delete_config_per_partner_domain',
                    parameters: [
                            string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                            string(name: 'SCENARIO', value: "${SCENARIO}"),
                            string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                            string(name: 'SZ_IP', value: "${szIP}"),
                            string(name: 'NPROC', value: "10"),
                    ],
                    propagate: false
        } else {
            echo "Skip to Create CSV"
        }
    }

    stage('Create CSV') {
        if (params.is_skip_csv == "false") {
            build job: 'create_csv', parameters: [
                    string(name: 'version', value: "${SZ_VERSION}"),
                    string(name: 'scenario', value: "${SCENARIO}"),
                    string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                    string(name: 'API_PERF_VER', value: "${API_PERF_VER}"),
                    string(name: 'DATA_DIR', value: "${DATA_DIR}"),
            ]
        } else {
            echo "Skip to Create CSV"
        }
    }

    stage('Clean Env') {
        if (params.is_clean_env == "true") {
            build job: 'suite_sz_teardown',
                    parameters: [
                            string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                            string(name: 'SCENARIO', value: "${SCENARIO}"),
                            string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                    ],
                    propagate: false

            build job: 'end2end_shutdown_sim',
                    parameters: [
                            string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                            string(name: 'SCENARIO', value: "${SCENARIO}"),
                            string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                    ],
                    propagate: false

            build job: 'end2end_delete_sim',
                    parameters: [
                            string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                            string(name: 'SCENARIO', value: "${SCENARIO}"),
                            string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                    ],
                    propagate: false
        } else {
            echo "Skip to Clean Env"
        }
    }
}
