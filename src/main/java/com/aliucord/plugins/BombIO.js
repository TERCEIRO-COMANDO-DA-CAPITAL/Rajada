package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.DimenUtils;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.chat.input.WidgetChatInputEditText$setOnTextChangedListener$1;
import com.lytefast.flexinput.fragment.FlexInputFragment;
import com.lytefast.flexinput.widget.FlexEditText;

@SuppressWarnings("unused")
@AliucordPlugin
public class BombIO extends Plugin {
    
    ImageButton rajadaButton;
    RajadaPanel rajadaPanel;
    FlexEditText editText;
    
    public static SettingsAPI staticSettings;
    
    @Override
    public void start(Context context) throws NoSuchMethodException {

        staticSettings = settings;
        
        // 1. Configurações (Abre o SettingsSheet.java)
        settingsTab = new SettingsTab(SettingsSheet.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);

        // 2. Criação do Botão Rajada (Ícone tipo WhatsApp)
        rajadaButton = new ImageButton(context);
        
        // **Substitua este recurso pelo ícone desejado (ex: um ícone de rajada/fogo)**
        var drawable = ContextCompat.getDrawable(context, com.aliucord.R.e.ic_menu_edit_white_24dp); 
        drawable.setTint(ColorCompat.getColor(context, com.lytefast.flexinput.R.c.primary_dark_300));
        rajadaButton.setImageDrawable(drawable);
        rajadaButton.setBackgroundColor(Color.TRANSPARENT);
        
        // 3. Ação do Botão: Abrir o Mini-Painel de Rajada
        rajadaButton.setOnClickListener(v -> {
            if (rajadaPanel == null) {
                rajadaPanel = new RajadaPanel(context, this);
            }
            rajadaPanel.toggleVisibility();
        });
        
        // 4. Patching para Injetar o Botão no Chat Input (Como no VoiceMessages.java)
        patcher.patch(FlexInputFragment.class.getDeclaredMethod("onViewCreated", View.class, Bundle.class), cf -> {
            var input = (FlexInputFragment) cf.thisObject;
            editText = input.getView().findViewById(Utils.getResId("text_input", "id"));
            
            var viewgroup = ((ViewGroup) input.getView().findViewById(Utils.getResId("main_input_container", "id")));
            
            // Adiciona o botão de rajada ao container
            viewgroup.addView(rajadaButton); 
            
            // Define o tamanho e gravidade do botão (ajuste conforme necessário)
            var params = (LinearLayout.LayoutParams) rajadaButton.getLayoutParams();
            params.height = DimenUtils.dpToPx(30);
            params.gravity = Gravity.CENTER;
        });

        // 5. Ocultar o Botão quando houver texto (Comportamento de cliente)
        patcher.patch(WidgetChatInputEditText$setOnTextChangedListener$1.class.getDeclaredMethod("afterTextChanged", Editable.class), cf -> {
            // Se a caixa de texto estiver vazia, mostra o botão de Rajada
            if (editText.getText() == null || editText.getText().toString().equals("")) {
                rajadaButton.setVisibility(View.VISIBLE);
            } else {
                rajadaButton.setVisibility(View.GONE);
            }
        });
    }

    // Método para executar o envio da rajada (chamado pelo RajadaPanel)
    public void startRajada(String message, int quantity, int delayMs) {
        // Envia a mensagem no canal atual usando uma Threadpool para não travar a UI
        Utils.threadPool.execute(() -> {
            long channelId = StoreStream.getChannelsSelected().getId();

            for (int i = 0; i < quantity; i++) {
                // Aqui você usaria a DiscordAPI para enviar a mensagem
                // Ex: DiscordAPI.sendMessage(message, channelId);

                // SIMULAÇÃO: Para fins práticos e de teste, usaremos o logger:
                logger.info("BOMB.IO RAJADA: Enviando mensagem " + (i + 1) + " de " + quantity + " em " + channelId);
                
                // Aplica o TEMPO (delay) solicitado pelo usuário
                try {
                    Thread.sleep(delayMs); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Rajada interrompida", e);
                    break;
                }
            }
            Utils.showToast("BOMB.IO: Rajada de " + quantity + " mensagens finalizada!");
        });
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
