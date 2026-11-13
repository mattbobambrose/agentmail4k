VERSION=$(shell grep '^version =' build.gradle.kts | head -1 | sed 's/.*"\(.*\)"/\1/' | sed 's/-SNAPSHOT//')

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
	./gradlew -PoverrideVersion=$(VERSION)-SNAPSHOT publishToMavenLocal

# Publishing credentials come from the macOS keychain and reach only the publishing commands, as ORG_GRADLE_PROJECT_*
# variables, never ~/.gradle/gradle.properties, which every Gradle build on the machine is given. Setup: README.md,
# "Publishing".

# Shell command printing a keychain entry's value: $(call keychain,<account>,<service>)
keychain = security find-generic-password -a "$(1)" -s "$(2)" -w

# Shell test failing, naming the entry and never its value, when a keychain entry is missing or empty
check_keychain = [ -n "$$($(call keychain,$(1),$(2)) 2>/dev/null)" ] || { \
	echo "ERROR: keychain entry '$(2)' (account '$(1)') is missing or empty; add it with: security add-generic-password -a $(1) -s $(2) -U -w" >&2; \
	exit 1; }

GPG_ENV = \
	ORG_GRADLE_PROJECT_signingInMemoryKey="$$(gpg --armor --export-secret-keys $$GPG_SIGNING_KEY_ID)" \
	ORG_GRADLE_PROJECT_signingInMemoryKeyPassword="$$($(call keychain,gpg-signing,gradle-signing-password))"

MAVEN_CENTRAL_ENV = \
	ORG_GRADLE_PROJECT_mavenCentralUsername="$$($(call keychain,maven-central,maven-central-username))" \
	ORG_GRADLE_PROJECT_mavenCentralPassword="$$($(call keychain,maven-central,maven-central-password))"

publish-snapshot: _check-publish-env
	$(GPG_ENV) $(MAVEN_CENTRAL_ENV) ./gradlew -PoverrideVersion=$(VERSION)-SNAPSHOT publishToMavenCentral

publish-maven-central: _check-publish-env
	$(GPG_ENV) $(MAVEN_CENTRAL_ENV) ./gradlew publishAndReleaseToMavenCentral

upgrade-wrapper:
	./gradlew wrapper --gradle-version=9.4.1 --distribution-type=bin

_check-publish-env:
	@[ -n "$$GPG_SIGNING_KEY_ID" ] || { echo "ERROR: GPG_SIGNING_KEY_ID is not set" >&2; exit 1; }
	@$(call check_keychain,gpg-signing,gradle-signing-password)
	@$(call check_keychain,maven-central,maven-central-username)
	@$(call check_keychain,maven-central,maven-central-password)
