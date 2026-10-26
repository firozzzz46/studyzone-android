
package com.firoz.studyzone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocalStore store;
    private LinearLayout root, content;
    private boolean dark;
    private String currentPage = "Home";
    private String updateVersion = "", updateNotes = "", updateApk = "";

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        store = new LocalStore(this); dark = store.isDark();
        showSplash();
        UpdateChecker.check((v,n,u) -> {
            if (!v.isEmpty() && compareVersions(v, AppConfig.VERSION) > 0) {
                updateVersion=v; updateNotes=n; updateApk=u;
                if ("Home".equals(currentPage)) showPage("Home");
            }
        });
    }
    private int compareVersions(String a, String b) {
        try { return new java.math.BigDecimal(a).compareTo(new java.math.BigDecimal(b)); }
        catch (Exception e) { return a.compareToIgnoreCase(b); }
    }
    private void showSplash() {
        LinearLayout l = Ui.column(this); l.setGravity(Gravity.CENTER);
        ImageView logo = new ImageView(this); logo.setImageResource(R.drawable.app_logo);
        l.addView(logo, new LinearLayout.LayoutParams(112,112));
        TextView name = Ui.text(this, AppConfig.APP_NAME, 30, Ui.color(AppConfig.GOLD), true); name.setGravity(Gravity.CENTER);
        l.addView(name); TextView tagline=Ui.text(this,"Your Journey of Learning Begins Here",15,Ui.color(AppConfig.WHITE),false); tagline.setGravity(Gravity.CENTER); l.addView(tagline);
        setContentView(l);
        l.postDelayed(() -> showWelcome(), 450);
    }
    private void showWelcome() {
        LinearLayout l = Ui.column(this); l.setGravity(Gravity.CENTER);
        ImageView logo = new ImageView(this); logo.setImageResource(R.drawable.app_logo); l.addView(logo,new LinearLayout.LayoutParams(96,96));
        TextView title=Ui.text(this,"Welcome to Study Zone",26,Ui.color(AppConfig.GOLD),true); title.setGravity(Gravity.CENTER); l.addView(title);
        TextView sub=Ui.text(this,"Islamic Study\nYour Journey of Learning Begins Here",16,Ui.color(AppConfig.WHITE),false); sub.setGravity(Gravity.CENTER); l.addView(sub);
        Ui.space(l,28); android.widget.Button go=Ui.button(this,"Continue"); l.addView(go,new LinearLayout.LayoutParams(-1,54));
        go.setOnClickListener(v -> showPage("Home")); setContentView(l);
    }
    private void setup(String page) {
        currentPage=page;
        root=Ui.column(this); root.setPadding(0,0,0,0);
        LinearLayout top=Ui.row(this); top.setPadding(18,10,18,10);
        TextView menu=Ui.text(this,"☰",26,Ui.color(AppConfig.GOLD),true); top.addView(menu,new LinearLayout.LayoutParams(44,48));
        menu.setOnClickListener(v -> showDrawer());
        TextView brand=Ui.text(this,"Study Zone  ·  "+page,18,Ui.color(AppConfig.WHITE),true); top.addView(brand,new LinearLayout.LayoutParams(0,48,1));
        root.addView(top);
        ScrollView scroll=new ScrollView(this);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(20,12,20,20);
        scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=Ui.row(this); nav.setPadding(6,6,6,6);
        String[] items={"Home","Website","Favorites","Downloads"};
        for(String item:items) {
            TextView t=Ui.text(this,item,12,Ui.color(AppConfig.GOLD),true); t.setGravity(Gravity.CENTER);
            nav.addView(t,new LinearLayout.LayoutParams(0,48,1));
            t.setOnClickListener(v -> { if ("Website".equals(item)) openWebsite(); else showPage(item); });
        }
        root.addView(nav); setContentView(root);
    }
    private void showPage(String page) {
        setup(page);
        switch(page) {
            case "Home": home(); break;
            case "Favorites": favorites(); break;
            case "Downloads": downloads(); break;
            case "About Me": about(); break;
            case "Settings": settings(); break;
            default: home();
        }
    }
    private void card(String title, String subtitle, Runnable click) {
        LinearLayout c=Ui.column(this); c.setPadding(16,12,16,12); c.setBackground(Ui.bg(AppConfig.NAVY,20,AppConfig.GOLD));
        TextView h=Ui.text(this,title,18,Ui.color(AppConfig.GOLD),true); c.addView(h);
        TextView s=Ui.text(this,subtitle,13,Ui.color(AppConfig.WHITE),false); c.addView(s);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.bottomMargin=12; content.addView(c,lp);
        c.setOnClickListener(v -> click.run());
    }
    private void home() {
        LinearLayout hero=Ui.column(this); hero.setGravity(Gravity.CENTER); hero.setBackground(Ui.bg(AppConfig.NAVY,24,AppConfig.GOLD));
        ImageView logo=new ImageView(this); logo.setImageResource(R.drawable.app_logo); hero.addView(logo,new LinearLayout.LayoutParams(78,78));
        TextView h=Ui.text(this,"Islamic Study",25,Ui.color(AppConfig.GOLD),true); h.setGravity(Gravity.CENTER); hero.addView(h);
        TextView s=Ui.text(this,"Welcome to Study Zone",16,Ui.color(AppConfig.WHITE),false); s.setGravity(Gravity.CENTER); hero.addView(s);
        content.addView(hero,new LinearLayout.LayoutParams(-1,-2)); Ui.space(content,16);
        card("🌐  Website","Open the main website inside the app",this::openWebsite);
        card("☆  Favorites","View your saved links",() -> showPage("Favorites"));
        card("⇩  Downloads","Files downloaded by the app are in Downloads/StudyZone",() -> showPage("Downloads"));
        if (!updateVersion.isEmpty()) card("Update Available · "+updateVersion,updateNotes.isEmpty()?"A newer version is available.":updateNotes, this::showUpdate);
        else card("Updates","Update checker is ready. Configure a real metadata URL in AppConfig.java.",() -> Toast.makeText(this,"Update metadata URL is not configured yet.",Toast.LENGTH_LONG).show());
    }
    private void openWebsite() {
        Intent i=new Intent(this,BrowserActivity.class); i.putExtra(BrowserActivity.EXTRA_URL,AppConfig.WEBSITE_URL); startActivity(i);
    }
    private void showDrawer() {
        String[] options={"Home","Website","Favorites","Downloads","About Me","Facebook","Share App","Settings"};
        new AlertDialog.Builder(this).setTitle("Study Zone Menu").setItems(options,(d,which)->{
            switch(options[which]) {
                case "Website": openWebsite(); break;
                case "Facebook": openExternal(AppConfig.FACEBOOK_URL); break;
                case "Share App": shareApp(); break;
                default: showPage(options[which]);
            }
        }).setNegativeButton("Close",null).show();
    }
    private void favorites() {
        card("＋ Add current website","Save the main website to Favorites",() -> {
            store.addFavorite("Islamic Study",AppConfig.WEBSITE_URL); showPage("Favorites");
        });
        List<String> items=store.getFavorites();
        if(items.isEmpty()) { card("No favorites yet","Save the main website or add links from this page.",()->{}); return; }
        for(int i=0;i<items.size();i++) {
            final int index=i;
            try {
                JSONObject o=new JSONObject(items.get(i));
                card(o.optString("title","Saved link"),o.optString("url"),()->new AlertDialog.Builder(this)
                    .setTitle(o.optString("title","Saved link")).setItems(new String[]{"Open","Delete"},(d,w)->{
                        if(w==0){ Intent in=new Intent(this,BrowserActivity.class); in.putExtra(BrowserActivity.EXTRA_URL,o.optString("url")); startActivity(in);}
                        else {store.removeFavorite(index); showPage("Favorites");}
                    }).show());
            } catch(Exception ignored) {}
        }
    }
    private void downloads() {
        card("Downloads folder","Files downloaded from websites are saved under the public Downloads/StudyZone folder. Android's system download notification shows progress and completion.",()->{
            Intent intent=new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("content://downloads/my_downloads"),"*/*");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try { startActivity(intent); } catch(Exception e) { Toast.makeText(this,"Open the Downloads app to manage files.",Toast.LENGTH_LONG).show(); }
        });
        card("Offline reading","Offline access depends on the website and its content. This app does not claim that every page can be saved for offline reading.",()->{});
    }
    private void about() {
        ImageView photo=new ImageView(this);
        try {
            java.io.InputStream in=getAssets().open("about_photo.jpg");
            photo.setImageBitmap(android.graphics.BitmapFactory.decodeStream(in)); in.close();
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            content.addView(photo,new LinearLayout.LayoutParams(-1,360));
        } catch(Exception e) { content.addView(Ui.text(this,"Photo asset not found. Add app/src/main/assets/about_photo.jpg",14,Ui.color(AppConfig.WHITE),false)); }
        TextView name=Ui.text(this,"Firoz Ahmad",24,Ui.color(AppConfig.GOLD),true); name.setGravity(Gravity.CENTER); content.addView(name);
    }
    private void settings() {
        card("Theme: "+(dark?"Dark":"Light"),"Tap to switch theme preference",()->{
            dark=!dark; store.setDark(dark);
            Toast.makeText(this,"Theme preference saved. The app currently uses the premium navy base palette.",Toast.LENGTH_LONG).show();
            showPage("Settings");
        });
        card("Storage","Favorites saved: "+store.getFavorites().size()+" · Downloads use Android's Download Manager.",()->{});
        card("App Info","Version "+AppConfig.VERSION+"\nDeveloper: "+AppConfig.DEVELOPER,()->{});
        card("Privacy","Favorites and theme preference are stored locally. Website content is loaded from "+AppConfig.WEBSITE_URL+". The app does not include analytics or account login.",()->{});
        card("Website","Open main website",this::openWebsite);
        card("Clear Data","Clear saved favorites and app preferences after confirmation",()->new AlertDialog.Builder(this)
            .setTitle("Clear app data?").setMessage("This removes saved favorites and local preferences. Downloaded files are not deleted.")
            .setNegativeButton("Cancel",null).setPositiveButton("Clear", (d,w)->{store.clearAppData(); dark=store.isDark(); showPage("Settings");}).show());
    }
    private void showUpdate() {
        if(updateApk.isEmpty() || !updateApk.startsWith("https://")) { Toast.makeText(this,"No valid update APK URL is configured.",Toast.LENGTH_LONG).show(); return; }
        new AlertDialog.Builder(this).setTitle("Study Zone update "+updateVersion).setMessage(updateNotes.isEmpty()?"A newer version is available.":updateNotes)
            .setNegativeButton("Later",null).setPositiveButton("Download", (d,w)->{
                android.app.DownloadManager.Request req=new android.app.DownloadManager.Request(Uri.parse(updateApk));
                req.setTitle("Study Zone update"); req.setDescription("Downloading update APK");
                req.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS,"StudyZone-update.apk");
                ((android.app.DownloadManager)getSystemService(DOWNLOAD_SERVICE)).enqueue(req);
                Toast.makeText(this,"APK downloading. Open it from Downloads to install; Android may ask permission.",Toast.LENGTH_LONG).show();
            }).show();
    }
    private void openExternal(String url) {
        try { startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))); }
        catch(Exception e) { Toast.makeText(this,"Could not open link.",Toast.LENGTH_LONG).show(); }
    }
    private void shareApp() {
        Intent i=new Intent(Intent.ACTION_SEND); i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,AppConfig.APP_NAME+" — Premium Islamic learning companion.\n"+AppConfig.WEBSITE_URL);
        startActivity(Intent.createChooser(i,"Share Study Zone"));
    }
    @Override public void onBackPressed() {
        if(!"Home".equals(currentPage)) { showPage("Home"); return; }
        new AlertDialog.Builder(this).setTitle("Exit Study Zone")
            .setMessage("Are you sure you want to leave Study Zone?")
            .setNegativeButton("Stay",null).setPositiveButton("Exit",(d,w)->finish()).show();
    }
}
