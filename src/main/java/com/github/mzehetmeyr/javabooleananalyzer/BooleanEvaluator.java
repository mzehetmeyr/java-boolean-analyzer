package com.github.mzehetmeyr.javabooleananalyzer;

import java.util.*;

public class BooleanEvaluator {

    private String expression;
    private int pos = -1, ch;
    private Map<Character, Boolean> variables;

    public static class TruthTableData {
        public List<String> headers;
        public List<List<String>> rows;

        public TruthTableData(List<String> headers, List<List<String>> rows) {
            this.headers = headers;
            this.rows = rows;
        }
    }

    // Retorna os dados formatados para o JavaFX TableView
    public TruthTableData generateTableData(String expr) {
        List<Character> vars = extractVariables(expr);
        List<String> headers = new ArrayList<>();

        // Cria os cabeçalhos
        for (char v : vars) {
            headers.add(String.valueOf(v));
        }
        headers.add("Resultado");

        List<List<String>> rows = new ArrayList<>();
        int numRows = (int) Math.pow(2, vars.size());

        // Gera as combinações
        for (int i = 0; i < numRows; i++) {
            Map<Character, Boolean> values = new HashMap<>();
            List<String> row = new ArrayList<>();

            for (int j = 0; j < vars.size(); j++) {
                boolean val = (i & (1 << (vars.size() - 1 - j))) != 0;
                values.put(vars.get(j), val);
                row.add(val ? "1" : "0");
            }

            try {
                boolean result = evaluate(expr, values);
                row.add(result ? "1" : "0");
            } catch (Exception e) {
                throw new RuntimeException("Erro de sintaxe na expressão!");
            }
            rows.add(row);
        }

        return new TruthTableData(headers, rows);
    }

    // Extrai as variáveis da expressão em ordem alfabética
    public static List<Character> extractVariables(String expression) {
        Set<Character> vars = new TreeSet<>();
        for (char c : expression.toCharArray()) {
            if (Character.isLetter(c)) {
                vars.add(Character.toUpperCase(c));
            }
        }
        return new ArrayList<>(vars);
    }

    // Inicia a avaliação de uma expressão matemática
    public boolean evaluate(String expr, Map<Character, Boolean> vars) {
        this.expression = expr;
        this.variables = vars;
        this.pos = -1;
        nextChar();
        boolean result = parseExpression();
        if (pos < expression.length()) throw new RuntimeException("Caractere inesperado: " + (char) ch);
        return result;
    }

    // LÓGICA DO PARSER
    private void nextChar() {
        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    // Nível 1: OR (+)
    private boolean parseExpression() {
        boolean x = parseTerm();
        while (true) {
            if (eat('+')) x = x | parseTerm();
            else return x;
        }
    }

    // Nível 2: AND (.)
    private boolean parseTerm() {
        boolean x = parseFactor();
        while (true) {
            if (eat('.')) x = x & parseFactor();
            else return x;
        }
    }

    // Nível 3: NOT (!), Parênteses () e Variáveis
    private boolean parseFactor() {
        while (ch == ' ') nextChar();

        if (eat('!')) return !parseFactor(); // Inverte o valor

        if (eat('(')) {
            boolean x = parseExpression();
            if (!eat(')')) throw new RuntimeException("Faltou fechar parêntese ')'");
            return x;
        }

        if (Character.isLetter(ch)) {
            char varName = Character.toUpperCase((char) ch);
            nextChar();
            return variables.getOrDefault(varName, false);
        }

        throw new RuntimeException("Caractere inesperado: " + (char) ch);
    }
}