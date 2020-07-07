def checkResponseStatus(String outputDir, String returnCode = '201') {
  def cmd1 = ["bash", "-c", "find ${outputDir} -name \\*.out -exec grep 'Response code:' {} \\; | wc -l"]
  
  def proc1 = Runtime.getRuntime().exec((String[]) cmd1.toArray())
  def totalResponse = proc1.text.trim() as Integer

  println cmd1
  println "total: ${totalResponse}"
  
  def cmd2 = ["bash", "-c", "find ${outputDir} -name \\*.out -exec grep 'Response code: ${returnCode}' {} \\; | wc -l"]
  def proc2 = Runtime.getRuntime().exec((String[]) cmd2.toArray())
  def successfulResponse = proc2.text.trim() as Integer

  println cmd2
  println "successful: ${successfulResponse}"
  
  def result = 'FAILURE'
  if (successfulResponse == 0) {
    result = 'FAILURE'
  }
  else if (successfulResponse == totalResponse) {
    result = 'SUCCESS'
  } else {
    result = 'UNSTABLE'
  }
  
  return result
}

def statisticizeResponse(String outputDir, String returnCode = '201', String utilCmd='statistics.awk') {
  def cmd = ["bash", "-c", "find ${outputDir} -name \\*.out -exec grep -A1 'Response code: ${returnCode}' {} \\; | awk '/Response time:/ {print \$3}' | ${utilCmd} "]
  def proc = Runtime.getRuntime().exec((String[]) cmd.toArray())
  content = proc.text
  
  println cmd
  println content

  def cmd0 = ["bash", "-c", "find ${outputDir} -name \\*.out -exec grep -H -E 'Response time|Response code' {} \\; >& ${outputDir}/response.txt"]
  def proc0 = Runtime.getRuntime().exec((String[]) cmd0.toArray())
  println cmd0
}
