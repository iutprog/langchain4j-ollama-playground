# lc4j-simplest

One class, no Spring: LangChain4j `OllamaChatModel` → local Ollama in Podman.

## Run
1. Find the address Ollama actually answers on:
   ```
   curl -6 http://[::1]:11434/api/tags
   curl -4 http://127.0.0.1:11434/api/tags
   ```
   If only IPv4 works: `set OLLAMA_BASE_URL=http://127.0.0.1:11434`
2. `mvn compile exec:java`

Other model: `set OLLAMA_MODEL=qwen2.5` (must match `ollama list`).

JDK 26 builds this with `--release 21`.

## Test for the external context

Test questions to try:

“Can I use the Ops Console on my phone?”
“Who owns a SEV-2 load-balancer incident that has been open for 3 hours?”
“Has version 5.0 been rolled back?”
“How much does Meridian charge per server?”
