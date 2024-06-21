package io.spring.ragoneo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
public class RagConfiguration {

    @Value("classpath:/prompts/BatchTalkPrompt.st")
    private Resource batchPromptTemplate;


    @Bean
    ApplicationRunner applicationRunner(ChatClient.Builder chatClientBuilder, JdbcClient jdbcClient, VectorStore vectorStore) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                PromptTemplate promptTemplate = new PromptTemplate(batchPromptTemplate);
                Map<String, Object> promptParameters = new HashMap<>();
//                String message = "Customer Jane has deposited $5000 please provide a 1 line summary of how much interest she can make in a money market.";
//                String message = "What is a group of wombats called?";
                String message = "What is this presentation about?";
                promptParameters.put("input", message);
                promptParameters.put("documents", String.join("\n", findSimilarDocuments(message, vectorStore)));
                printResult(chatClientBuilder, promptParameters, promptTemplate);

                message = "How do I build the project?";
                promptParameters.put("input", message);
                promptParameters.put("documents", String.join("\n", findSimilarDocuments(message, vectorStore)));
                printResult(chatClientBuilder, promptParameters, promptTemplate);

                message = "Can Spring Batch simplify the loading of Vector DBs?  And how?";
                promptParameters.put("input", message);
                promptParameters.put("documents", String.join("\n", findSimilarDocuments(message, vectorStore)));
                printResult(chatClientBuilder, promptParameters, promptTemplate);

                message = "What is a wombat?";
                promptParameters.put("input", message);
                promptParameters.put("documents", String.join("\n", findSimilarDocuments(message, vectorStore)));
                printResult(chatClientBuilder, promptParameters, promptTemplate);


            }
        };
    }

    private void printResult(ChatClient.Builder chatClientBuilder, Map<String, Object> promptParameters, PromptTemplate promptTemplate) {
        ChatClient  chatClient = chatClientBuilder.build();
        System.out.println("Prompt = " + promptParameters.get("input"));
//                System.out.println(chatClient.prompt().user("what day is it?").call().content());
//                System.out.println(promptParameters.get("documents"));
        System.out.println(chatClient.prompt(promptTemplate.create(promptParameters)).call().content());
        System.out.println("\n");

    }
    private List<String> findSimilarDocuments(String message, VectorStore vectorStore) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        return similarDocuments.stream().map(Document::getContent).toList();
    }
}
