package br.com.ilink.zenviaapisoap.models.enums;

/**
 * Tipo de status de entrega. ALL: Envia status intermediários e final da mensagem. FINAL: Envia o
 * status final de entrega da mensagem (recomendado). NONE: Não será feito callback do status de
 * entrega.
 */
public enum CallbackOptionEnum {
  ALL, FINAL, NONE
}
