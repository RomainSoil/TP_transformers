package org.example;

public class LLMTest {

    public static void sample1() throws Exception {
        LLMClient llm = new LLMClient("phi-3");
        String response = llm.ask("Calcule 25 * (4 + 6)", 0.0);
        System.out.println(response);
    }
}
