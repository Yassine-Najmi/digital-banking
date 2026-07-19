package ma.enset.ebankbackend.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;

@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);

    @Value("${ebank.rag.vector-store-path}")
    private String vectorStorePath;

    @Value("${ebank.rag.document}")
    private String ragDocumentLocation;

    @Value("${spring.ai.openai.api-key:}")
    private String openAiApiKey;

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        if (!StringUtils.hasText(openAiApiKey) || openAiApiKey.contains("${") || "sk-votre-cle-ici".equals(openAiApiKey)) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY est absente ou invalide. Définissez la variable d'environnement avant de démarrer (voir .env.example)."
            );
        }

        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File storeFile = new File(vectorStorePath);

        if (storeFile.exists()) {
            log.info("Chargement du vector store depuis {}", storeFile.getAbsolutePath());
            vectorStore.load(storeFile);
            return vectorStore;
        }

        log.info("Ingestion du document RAG {} (premier démarrage)", ragDocumentLocation);
        File parent = storeFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Impossible de créer le dossier " + parent.getAbsolutePath());
        }

        MarkdownDocumentReader reader = new MarkdownDocumentReader(
                ragDocumentLocation,
                MarkdownDocumentReaderConfig.builder().build()
        );
        List<Document> chunks = new TokenTextSplitter().apply(reader.get());
        vectorStore.add(chunks);
        vectorStore.save(storeFile);
        log.info("Vector store persisté ({} chunks) dans {}", chunks.size(), storeFile.getAbsolutePath());
        return vectorStore;
    }
}
