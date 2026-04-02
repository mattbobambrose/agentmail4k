default: versioncheck

clean:
	./gradlew clean

build: clean
	./gradlew build

versioncheck:
	./gradlew dependencyUpdates

docker-local: build
	docker buildx build --platform linux/amd64,linux/arm64 -t mattbobambrose/eocare-pipeline .

docker-push: build
	docker buildx build --platform linux/amd64,linux/arm64 -t mattbobambrose/eocare-pipeline --push .

deploy: docker-push
	bash secrets/deploy-app.sh
	say "Deployed to Digital Ocean"

depends:
	./gradlew dependencies

run:
	./gradlew run

kdocs:
	./gradlew :dokkaGenerate

site:
	rm -rf website/agentmail4k/site
	rm -rf website/agentmail4k/.cache
	cd website/agentmail4k && uv run zensical serve

tests:
	./gradlew --rerun-tasks check

maven-central:
	./gradlew publishAndReleaseToMavenCentral

upgrade-wrapper:
	./gradlew wrapper --gradle-version=9.4.1 --distribution-type=bin
