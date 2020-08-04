package org.chelak.colorlines;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.chelak.colorlines.base.BaseActivity;
import org.chelak.colorlines.dialogs.GameOverDialog;

public class ColorLinesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_lines_activity);
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        {
            drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                    drawerLayout.bringChildToFront(drawerView);
                    drawerLayout.requestLayout();
                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }

        final GameBoardFragment gameBoard = GameBoardFragment.getInstance();
        gameBoard.setListener(new GameBoardFragment.GameEventListener() {
            @Override
            public void onGameOver() {
                // TODO
            }

            @Override
            public void onGameStarted(long mode) {
                int resource = R.drawable.background_m4;
                if (mode == 1)
                    resource = R.drawable.background_m2;
                else if (mode == 2)
                    resource = R.drawable.background_m1;
                setupBackground(resource);
            }

            @Override
            public void onMenuSelected() {
                if (!drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Button[] modeButtons = {
                findViewById(R.id.buttonLines),
                findViewById(R.id.buttonSquares),
                findViewById(R.id.buttonRings)
        };
        for (Button btn : modeButtons) {
            final Button button = btn;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    gameBoard.onButtonModeClick(button);
                }
            });
        }

        ImageButton btn = findViewById(R.id.btnFacebook);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                GameOverDialog.openFacebookPublic(ColorLinesActivity.this);
            }
        });

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, gameBoard)
                .commit();
    }


}
