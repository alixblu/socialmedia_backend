from typing import Any, Dict, List, Text

import requests
from rasa.engine.graph import GraphComponent, ExecutionContext
from rasa.engine.recipes.default_recipe import DefaultV1Recipe
from rasa.engine.storage.resource import Resource
from rasa.engine.storage.storage import ModelStorage
from rasa.shared.nlu.training_data.message import Message
from rasa.shared.nlu.training_data.training_data import TrainingData
from rasa.nlu.tokenizers.tokenizer import Tokenizer
from rasa.nlu.tokenizers.whitespace_tokenizer import WhitespaceTokenizer

@DefaultV1Recipe.register(
    [DefaultV1Recipe.ComponentType.INTENT_CLASSIFIER, DefaultV1Recipe.ComponentType.ENTITY_EXTRACTOR],
    is_trainable=False
)
class OllamaNLP(GraphComponent):
    def __init__(
        self,
        config: Dict[Text, Any],
        model_storage: ModelStorage,
        resource: Resource,
        execution_context: ExecutionContext,
    ) -> None:
        self.model = config.get("model", "phi")
        self.base_url = config.get("base_url", "http://localhost:11434")
        self.tokenizer = WhitespaceTokenizer()

    @classmethod
    def create(
        cls,
        config: Dict[Text, Any],
        model_storage: ModelStorage,
        resource: Resource,
        execution_context: ExecutionContext,
    ) -> "OllamaNLP":
        return cls(config, model_storage, resource, execution_context)

    def train(self, training_data: TrainingData) -> Resource:
        # No training needed as we're using Ollama directly
        return self.resource

    def process(self, messages: List[Message]) -> List[Message]:
        for message in messages:
            if message.get("text"):
                # Tokenize the text
                tokens = self.tokenizer.tokenize(message.get("text"))
                
                # Prepare the prompt for Ollama
                prompt = f"Classify the intent and extract entities from: {message.get('text')}"
                
                try:
                    # Call Ollama API
                    response = requests.post(
                        f"{self.base_url}/api/generate",
                        json={
                            "model": self.model,
                            "prompt": prompt,
                            "stream": False
                        }
                    )
                    
                    if response.status_code == 200:
                        result = response.json()
                        # Process the response and set intent and entities
                        # This is a simplified example - you'll need to parse the response
                        # based on your specific needs
                        message.set("intent", {"name": "general", "confidence": 1.0})
                        message.set("entities", [])
                    else:
                        # Handle error
                        message.set("intent", {"name": "error", "confidence": 0.0})
                        message.set("entities", [])
                except Exception as e:
                    # Handle exception
                    message.set("intent", {"name": "error", "confidence": 0.0})
                    message.set("entities", [])
        
        return messages 