default: versioncheck

clean:
	./gradlew clean

build: clean
	./gradlew build

tests:
	./gradlew --rerun-tasks check

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
	./gradlew dokkaGeneratePublicationHtml

clean-docs:
	rm -rf website/agentmail4k/site
	rm -rf website/agentmail4k/.cache

site: clean-docs
	cd website/agentmail4k && uv run zensical serve

publish-local:
	./gradlew publishToMavenLocal

publish-local-snapshot:
	$(eval BASE_VERSION := $(shell grep '^version =' build.gradle.kts | sed 's/.*"\(.*\)"/\1/' | sed 's/-SNAPSHOT//'))
	./gradlew -PoverrideVersion=$(BASE_VERSION)-SNAPSHOT publishToMavenLocal

publish-snapshot:
	$(eval BASE_VERSION := $(shell grep '^version =' build.gradle.kts | sed 's/.*"\(.*\)"/\1/' | sed 's/-SNAPSHOT//'))
	ORG_GRADLE_PROJECT_signingInMemoryKey="$$(gpg --armor --export-secret-keys E4467B8F)" \
	ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=$$(security find-generic-password -a "gpg-signing" -s "gradle-signing-password" -w) \
	./gradlew -PoverrideVersion=$(BASE_VERSION)-SNAPSHOT publishToMavenCentral

publish-maven-central:
	ORG_GRADLE_PROJECT_signingInMemoryKey="$$(gpg --armor --export-secret-keys E4467B8F)" \
	ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=$$(security find-generic-password -a "gpg-signing" -s "gradle-signing-password" -w) \
	./gradlew publishAndReleaseToMavenCentral

upgrade-wrapper:
	./gradlew wrapper --gradle-version=9.4.1 --distribution-type=bin
