package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aliucord.utils.DimenUtils;

public class RajadaPanel extends LinearLayout {
    
    private final BombIO mainPlugin;
    private final EditText messageInput; // 1 - Mensagem
    private final EditText quantityInput; // 2 - Quantidade
    private final EditText delayInput;    // 3 - Tempo
    
    public RajadaPanel(Context context, BombIO plugin) {
        super(context);
        this.mainPlugin = plugin;
        setOrientation(VERTICAL);
        // Estilo e Cor do Fundo (pega da config)
        setBackgroundColor(Color.parseColor(BombIO.staticSettings.getString("painelCorFundo", "#AA000000"))); // Preto semi-transparente
        setPadding(DimenUtils.dpToPx(12), DimenUtils.dpToPx(12), DimenUtils.dpToPx(12), DimenUtils.dpToPx(12));
        setGravity(Gravity.CENTER_HORIZONTAL);
        
        // **Mini-Painel de Rajada**
        
        // Título
        TextView title = new TextView(context);
        title.setText("💣 BOMB.IO - Rajada");
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        addView(title);

        // 1. Mensagem
        messageInput = new EditText(context);
        messageInput.setHint("1 - Mensagem");
        messageInput.setHintTextColor(Color.GRAY);
        messageInput.setTextColor(Color.WHITE);
        addView(messageInput);

        // 2. Quantidade
        quantityInput = new EditText(context);
        quantityInput.setHint("2 - Quantidade (ex: 5)");
        quantityInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        quantityInput.setText("5"); // Valor padrão
        quantityInput.setTextColor(Color.WHITE);
        addView(quantityInput);

        // 3. Tempo
        delayInput = new EditText(context);
        delayInput.setHint("3 - Tempo (ms) (ex: 1000)");
        delayInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        delayInput.setText("500"); // Valor padrão
        delayInput.setTextColor(Color.WHITE);
        addView(delayInput);
        
        // 4. Botões (Minimizar / Enviar)
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        
        Button minimizeButton = new Button(context);
        minimizeButton.setText("Minimizar");
        minimizeButton.setOnClickListener(v -> toggleVisibility());
        buttonLayout.addView(minimizeButton);

        Button sendButton = new Button(context);
        sendButton.setText("Enviar");
        sendButton.setBackgroundColor(Color.parseColor("#FF0077")); // Cor personalizada
        sendButton.setOnClickListener(v -> {
            try {
                mainPlugin.startRajada(
                    messageInput.getText().toString(),
                    Integer.parseInt(quantityInput.getText().toString()),
                    Integer.parseInt(delayInput.getText().toString())
                );
                toggleVisibility();
            } catch (NumberFormatException e) {
                com.aliucord.Utils.showToast("Quantidade e Tempo devem ser números válidos!");
            }
        });
        buttonLayout.addView(sendButton);

        addView(buttonLayout);
        setVisibility(GONE); // Começa invisível
    }

    public void toggleVisibility() {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }
}
