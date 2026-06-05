package site.otools.Wasted.screen.custom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import site.otools.Wasted.block.entity.CasinoBlockEntity;
import site.otools.Wasted.casino.Blackjack;

public class CasinoScreen extends AbstractContainerScreen<CasinoMenu> {


    private static final int FRAME       = 0xFF120D07;
    private static final int FRAME_HI    = 0xFF2A2012;
    private static final int GOLD        = 0xFFC9A227;
    private static final int GOLD_HI     = 0xFFE9CF6A;
    private static final int GOLD_DK     = 0xFF8C6F16;
    private static final int FELT        = 0xFF15692F;
    private static final int FELT_HI     = 0xFF1C8038;
    private static final int FELT_DK     = 0xFF0E4D22;
    private static final int WOOD        = 0xFF3B2B1A;
    private static final int WOOD_HI     = 0xFF54402A;
    private static final int PANEL       = 0xFF0B2A15;
    private static final int TEXT        = 0xFFF3ECD6;
    private static final int RED         = 0xFFC8141E;

    private static final int CARD_W = 18;
    private static final int CARD_H = 26;
    private static final int CARD_GAP = 20;
    private static final int HAND_X = 46;


    private static final String[] HEART = {"0110110","1111111","1111111","1111111","0111110","0011100","0001000"};
    private static final String[] DIAMOND = {"0001000","0011100","0111110","1111111","0111110","0011100","0001000"};
    private static final String[] SPADE = {"0001000","0011100","0111110","1111111","1111111","0010100","0011100"};
    private static final String[] CLUB = {"0011100","0011100","1011101","1111111","1111111","0011100","0111110"};

    private Button betMinus10, betMinus, betPlus, betPlus10, deal, hit, stand;

    public CasinoScreen(CasinoMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos, y = topPos;
        betMinus10 = addRenderableWidget(Button.builder(Component.literal("-10"), b -> press(CasinoMenu.BTN_BET_MINUS_10))
                .bounds(x + 46, y + 122, 22, 16).build());
        betMinus = addRenderableWidget(Button.builder(Component.literal("-"), b -> press(CasinoMenu.BTN_BET_MINUS))
                .bounds(x + 69, y + 122, 16, 16).build());
        betPlus = addRenderableWidget(Button.builder(Component.literal("+"), b -> press(CasinoMenu.BTN_BET_PLUS))
                .bounds(x + 86, y + 122, 16, 16).build());
        betPlus10 = addRenderableWidget(Button.builder(Component.literal("+10"), b -> press(CasinoMenu.BTN_BET_PLUS_10))
                .bounds(x + 103, y + 122, 22, 16).build());
        deal = addRenderableWidget(Button.builder(Component.translatable("gui.wastedmod.casino.deal"), b -> press(CasinoMenu.BTN_DEAL))
                .bounds(x + 150, y + 122, 30, 16).build());
        hit = addRenderableWidget(Button.builder(Component.translatable("gui.wastedmod.casino.hit"), b -> press(CasinoMenu.BTN_HIT))
                .bounds(x + 182, y + 122, 28, 16).build());
        stand = addRenderableWidget(Button.builder(Component.translatable("gui.wastedmod.casino.stand"), b -> press(CasinoMenu.BTN_STAND))
                .bounds(x + 212, y + 122, 36, 16).build());
        updateButtons();
    }

    private void press(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    private void updateButtons() {
        boolean hasGame = menu.gameId() != CasinoBlockEntity.GAME_NONE;
        boolean betting = menu.phase() != CasinoBlockEntity.PHASE_PLAYER;
        boolean playing = menu.phase() == CasinoBlockEntity.PHASE_PLAYER;
        boolean canDeal = hasGame && betting && menu.bet() >= 1 && menu.coins() >= menu.bet();
        for (Button b : new Button[]{betMinus10, betMinus, betPlus, betPlus10, deal, hit, stand}) {
            if (b != null) b.visible = hasGame;
        }
        if (!hasGame) return;
        betMinus10.active = betting;
        betMinus.active = betting;
        betPlus.active = betting;
        betPlus10.active = betting;
        deal.active = canDeal;
        hit.active = playing;
        stand.active = playing;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;


        g.fill(x, y, x + imageWidth, y + imageHeight, FRAME);
        g.fill(x + 1, y + 1, x + imageWidth - 1, y + 2, FRAME_HI);
        g.fill(x + 1, y + 1, x + 2, y + imageHeight - 1, FRAME_HI);


        g.fill(x + 3, y + 3, x + imageWidth - 3, y + 17, PANEL);
        goldLine(g, x + 3, y + 17, imageWidth - 6);


        panel(g, x + 4, y + 20, 32, 98, WOOD, WOOD_HI);
        g.drawString(font, "KEY", x + 11, y + 22, GOLD_HI, false);
        g.drawString(font, "$", x + 19, y + 50, GOLD_HI, false);
        bevelSlot(g, x + 13, y + 30);  // key slot
        bevelSlot(g, x + 13, y + 58);  // coin slot


        panel(g, x + 40, y + 20, imageWidth - 44, 98, FELT, FELT_HI);
        g.fill(x + 43, y + 23, x + imageWidth - 7, y + 24, FELT_HI);          // top sheen
        g.fill(x + 43, y + 114, x + imageWidth - 7, y + 115, FELT_DK);        // bottom shade


        panel(g, x + 4, y + imageHeight - 96, imageWidth - 8, 92, WOOD, WOOD_HI);
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                bevelSlot(g, x + 48 + col * 18, y + 140 + row * 18);
        for (int col = 0; col < 9; col++)
            bevelSlot(g, x + 48 + col * 18, y + 198);

        if (menu.gameId() == CasinoBlockEntity.GAME_NONE) {
            g.drawCenteredString(font, Component.translatable("gui.wastedmod.casino.insert_key"),
                    x + 145, y + 60, GOLD_HI);
            g.drawString(font, "<<", x + 41, y + 36, GOLD_HI, false);
            return;
        }
        renderBlackjack(g, x, y);
    }

    private void renderBlackjack(GuiGraphics g, int x, int y) {
        // Dealer
        g.drawString(font, Component.translatable("gui.wastedmod.casino.dealer"), x + HAND_X, y + 24, TEXT, false);
        if (!menu.holeHidden() && menu.dealerCount() > 0) {
            badge(g, x + HAND_X + 44, y + 22, "" + menu.dealerTotal());
        }
        for (int i = 0; i < menu.dealerCount(); i++) {
            drawCard(g, x + HAND_X + i * CARD_GAP, y + 34, menu.dealerCard(i));
        }

        // Player
        g.drawString(font, Component.translatable("gui.wastedmod.casino.you"), x + HAND_X, y + 65, TEXT, false);
        if (menu.playerCount() > 0) {
            badge(g, x + HAND_X + 30, y + 63, "" + menu.playerTotal());
        }
        for (int i = 0; i < menu.playerCount(); i++) {
            drawCard(g, x + HAND_X + i * CARD_GAP, y + 75, menu.playerCard(i));
        }

        // bottom info bar
        int barY = y + 103;
        g.fill(x + 43, barY, x + imageWidth - 7, barY + 13, 0xCC0A2410);
        goldLine(g, x + 43, barY, imageWidth - 50);
        g.drawString(font, "Bet ", x + 47, barY + 3, TEXT, false);
        g.drawString(font, "" + menu.bet(), x + 67, barY + 3, GOLD_HI, false);
        g.drawString(font, "Coins ", x + 92, barY + 3, TEXT, false);
        g.drawString(font, "" + menu.coins(), x + 122, barY + 3, GOLD_HI, false);

        int res = menu.result();
        if (res != CasinoBlockEntity.RESULT_NONE && menu.phase() == CasinoBlockEntity.PHASE_RESOLVED) {
            String key = switch (res) {
                case CasinoBlockEntity.RESULT_WIN -> "gui.wastedmod.casino.result.win";
                case CasinoBlockEntity.RESULT_LOSE -> "gui.wastedmod.casino.result.lose";
                case CasinoBlockEntity.RESULT_PUSH -> "gui.wastedmod.casino.result.push";
                case CasinoBlockEntity.RESULT_BLACKJACK -> "gui.wastedmod.casino.result.blackjack";
                case CasinoBlockEntity.RESULT_BUST -> "gui.wastedmod.casino.result.bust";
                default -> "";
            };
            int color = (res == CasinoBlockEntity.RESULT_WIN || res == CasinoBlockEntity.RESULT_BLACKJACK)
                    ? 0xFF6BE06B : (res == CasinoBlockEntity.RESULT_PUSH ? GOLD_HI : 0xFFE86B6B);
            Component c = Component.translatable(key);
            g.drawString(font, c, x + imageWidth - 11 - font.width(c), barY + 3, color, false);
        }
    }



    private void panel(GuiGraphics g, int x, int y, int w, int h, int base, int hi) {
        g.fill(x, y, x + w, y + h, GOLD_DK);            // gold trim
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, base);
        g.fill(x + 1, y + 1, x + w - 1, y + 2, hi);     // top sheen
        g.fill(x + 1, y + 1, x + 2, y + h - 1, hi);     // left sheen
    }

    private void goldLine(GuiGraphics g, int x, int y, int w) {
        g.fill(x, y, x + w, y + 1, GOLD);
        g.fill(x, y + 1, x + w, y + 2, GOLD_DK);
    }

    private void bevelSlot(GuiGraphics g, int x, int y) {
        g.fill(x - 1, y - 1, x + 17, y + 17, GOLD_DK);
        g.fill(x - 1, y - 1, x + 17, y, GOLD);
        g.fill(x - 1, y - 1, x, y + 17, GOLD);
        g.fill(x, y, x + 16, y + 16, 0xFF1B160E);
    }

    private void badge(GuiGraphics g, int x, int y, String text) {
        int w = font.width(text) + 6;
        g.fill(x, y, x + w, y + 11, 0xFF120D07);
        g.fill(x, y, x + w, y + 1, GOLD);
        g.drawString(font, text, x + 3, y + 2, GOLD_HI, false);
    }

    private void drawCard(GuiGraphics g, int x, int y, int card) {
        if (card == CasinoBlockEntity.CARD_EMPTY) return;
        // shadow
        g.fill(x + 1, y + 1, x + CARD_W + 1, y + CARD_H + 1, 0x66000000);

        if (card == CasinoBlockEntity.CARD_FACE_DOWN) {
            g.fill(x, y, x + CARD_W, y + CARD_H, GOLD);
            g.fill(x + 1, y + 1, x + CARD_W - 1, y + CARD_H - 1, 0xFF7A1222);
            g.fill(x + 3, y + 3, x + CARD_W - 3, y + CARD_H - 3, 0xFF9C2334);
            drawPip(g, x + CARD_W / 2 - 3, y + CARD_H / 2 - 3, DIAMOND, GOLD_HI);
            return;
        }
        // white body
        g.fill(x, y, x + CARD_W, y + CARD_H, 0xFFBFBFBF);
        g.fill(x + 1, y + 1, x + CARD_W - 1, y + CARD_H - 1, 0xFFFBFBF7);

        int suit = Blackjack.suit(card);
        boolean red = suit == 0 || suit == 1;
        int color = red ? RED : 0xFF14140F;
        String[] pip = switch (suit) { case 0 -> HEART; case 1 -> DIAMOND; case 2 -> CLUB; default -> SPADE; };
        String rank = rankString(Blackjack.rank(card));

        g.drawString(font, rank, x + 2, y + 2, color, false);
        drawPip(g, x + CARD_W / 2 - 3, y + 9, pip, color);
        g.drawString(font, rank, x + CARD_W - 1 - font.width(rank), y + CARD_H - 9, color, false);
    }

    private void drawPip(GuiGraphics g, int x, int y, String[] pattern, int color) {
        for (int r = 0; r < pattern.length; r++) {
            String row = pattern[r];
            for (int c = 0; c < row.length(); c++) {
                if (row.charAt(c) == '1') g.fill(x + c, y + r, x + c + 1, y + r + 1, color);
            }
        }
    }

    private static String rankString(int rank) {
        return switch (rank) {
            case 0 -> "A";
            case 9 -> "10";
            case 10 -> "J";
            case 11 -> "Q";
            case 12 -> "K";
            default -> String.valueOf(rank + 1);
        };
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, titleLabelX, titleLabelY, GOLD_HI, false);
        g.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);
    }
}
