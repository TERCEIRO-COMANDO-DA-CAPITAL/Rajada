package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.widgets.BottomSheet;
import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;

import java.util.Arrays;
import java.util.List;

public class SettingsSheet extends BottomSheet {

    SettingsAPI settings;

    public SettingsSheet(SettingsAPI settings) {
        this.settings = settings;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        var context = requireContext();
        setPadding(20);

        // --- SEÇÃO DE TEMA E CORES ---
        TextView header1 = new TextView(context);
        header1.setText("🎨 Temas e Cores");
        header1.setTextSize(18);
        header1.setTextColor(Color.parseColor("#FF0077")); // Cor de destaque personalizada
        addView(header1);

        // Opção: Cor de Fundo do Painel
        CheckedSetting corFundo = Utils.createCheckedSetting(context, CheckedSetting.ViewType.RADIO, "Cor Fundo Painel", "Ex: #AA000000");
        corFundo.setChecked(settings.getString("painelCorFundo", "#AA000000").equals("#AA000000"));

        corFundo.setOnCheckedListener(aBoolean -> {
            // Em uma implementação real, você usaria um ColorPicker para configurar a cor exata
            settings.setString("painelCorFundo", aBoolean ? "#AA000000" : "#CC330033");
            Utils.showToast("Cor do Painel alterada! Reinicie o plugin.");
        });
        addView(corFundo);


        // --- SEÇÃO DE EFEITOS ESPECIAIS (BLUR / ONG) ---
        TextView header2 = new TextView(context);
        header2.setText("✨ Efeitos Avançados (Blur/ONG)");
        header2.setTextSize(18);
        header2.setTextColor(Color.WHITE);
        addView(header2);
        
        // Opção: Efeito Blur no Fundo
        CheckedSetting blurEffect = Utils.createCheckedSetting(context, CheckedSetting.ViewType.CHECK, "Efeito Blur (Desfoque)", "Aplica desfoque no fundo do painel.");
        blurEffect.setChecked(settings.getBool("ativarBlur", false));

        blurEffect.setOnCheckedListener(aBoolean -> {
            settings.setBool("ativarBlur", aBoolean);
            Utils.showToast("Blur: " + (aBoolean ? "Ativado" : "Desativado") + ". (Requer implementação de Blur no Android)");
        });
        addView(blurEffect);
        
        // Opção: Opção ONG (Geralmente uma sigla para algum recurso específico do mod)
        CheckedSetting ongSetting = Utils.createCheckedSetting(context, CheckedSetting.ViewType.CHECK, "Recurso ONG", "Ativa o recurso de otimização de rede (Placeholder)");
        ongSetting.setChecked(settings.getBool("ativarONG", false));

        ongSetting.setOnCheckedListener(aBoolean -> {
            settings.setBool("ativarONG", aBoolean);
        });
        addView(ongSetting);

        // --- SEÇÃO DE PREVENÇÃO DE BANIMENTO ---
        TextView header3 = new TextView(context);
        header3.setText("🛡️ Prevenção");
        header3.setTextSize(18);
        header3.setTextColor(Color.YELLOW);
        addView(header3);

        // Nível de Segurança (Rajada)
        List<CheckedSetting> radios = Arrays.asList(
                Utils.createCheckedSetting(context, CheckedSetting.ViewType.RADIO, "Alto (Delay > 1s)", null),
                Utils.createCheckedSetting(context, CheckedSetting.ViewType.RADIO, "Médio (Delay > 500ms)", null),
                Utils.createCheckedSetting(context, CheckedSetting.ViewType.RADIO, "Baixo (Delay < 500ms)", null)
        );

        RadioManager radioManager = new RadioManager(radios);
        var segurancaLevels = Arrays.asList("ALTO", "MEDIO", "BAIXO");

        var selectedRadio = radios.get(segurancaLevels.indexOf(settings.getString("nivelSeguranca", "MEDIO")));
        selectedRadio.setChecked(true);
        radioManager.a(selectedRadio);

        for (int i = 0; i < radios.size(); i++) {
            int finalSize = i;
            CheckedSetting radio = radios.get(i);
            radio.e(e -> {
                settings.setString("nivelSeguranca", segurancaLevels.get(finalSize));
                radioManager.a(radio);
            });
            addView(radio);
        }

    }
}
