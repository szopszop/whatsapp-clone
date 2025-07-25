$services = @("auth-server", "gatewayserver", "configserver", "eurekaserver", "user-service", "message-service")

foreach ($s in $services) {
    $imageFullName = "szopszop/$s"

    try {
        $imageExists = docker image inspect $imageFullName 2>$null
    } catch {
    }

    if ($imageExists) {
        docker rmi -f $imageFullName
    }

    Set-Location -Path $s
    mvn compile jib:dockerBuild
    Set-Location -Path ..
}