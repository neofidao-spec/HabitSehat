#!/usr/bin/env python3
"""Generate Play Store assets for HabitSehat"""
from PIL import Image, ImageDraw, ImageFont
import os

OUTPUT_DIR = "/data/data/com.termux/files/home/workspace/HabitSehat/play-store-assets"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Color palette - using the app's primary mint/green theme
PRIMARY = "#00BFA6"      # Teal/Mint primary
PRIMARY_DARK = "#009688"
BACKGROUND = "#FFFFFF"
TEXT_DARK = "#1A1A2E"
TEXT_LIGHT = "#FFFFFF"
ACCENT = "#FF6B6B"       # Coral accent for water
ACCENT2 = "#4ECDC4"      # Teal accent

def get_font(size, bold=False):
    """Get a font, fallback to default"""
    try:
        if bold:
            return ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", size)
        return ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", size)
    except:
        return ImageFont.load_default()

# ============================================================
# 1. APP ICON - 512x512 (used for Play Store listing)
# ============================================================
def create_app_icon():
    size = 512
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Background circle with gradient effect
    margin = 40
    draw.ellipse([margin, margin, size-margin, size-margin], 
                 fill=PRIMARY, outline=None)
    
    # Inner lighter circle for depth
    inner_margin = 80
    draw.ellipse([inner_margin, inner_margin, size-inner_margin, size-inner_margin], 
                 fill="#26D5BE")
    
    # Habit streak icon - flame/fire
    center = size // 2
    flame_points = [
        (center, center - 100),      # top
        (center - 30, center - 20),  # left curve
        (center - 15, center + 10),  # left inner
        (center, center + 60),       # bottom center
        (center + 15, center + 10),  # right inner
        (center + 30, center - 20),  # right curve
    ]
    draw.polygon(flame_points, fill="#FF6B35", outline="#E85D25", width=3)
    
    # Small flame inside
    small_flame = [
        (center, center - 60),
        (center - 15, center),
        (center, center + 30),
        (center + 15, center),
    ]
    draw.polygon(small_flame, fill="#FFD93D")
    
    # Water drop on the side
    drop_x = center + 100
    drop_y = center - 40
    drop_points = [
        (drop_x, drop_y - 30),
        (drop_x - 20, drop_y + 10),
        (drop_x, drop_y + 35),
        (drop_x + 20, drop_y + 10),
    ]
    draw.polygon(drop_points, fill="#4ECDC4", outline="#26B8A0", width=2)
    
    # Check mark (habit completion)
    check_x = center - 100
    check_y = center + 40
    draw.line([(check_x - 15, check_y), (check_x - 5, check_y + 10), (check_x + 15, check_y - 15)], 
              fill="#FFD93D", width=8)
    
    # Save
    img.save(f"{OUTPUT_DIR}/icon-512.png", "PNG")
    print("✅ Created icon-512.png")
    
    # Also create adaptive icon foreground (432x432 for Android adaptive icon)
    adaptive = img.resize((432, 432), Image.LANCZOS)
    adaptive.save(f"{OUTPUT_DIR}/ic_launcher_foreground.png", "PNG")
    print("✅ Created ic_launcher_foreground.png (adaptive icon)")
    
    return img

# ============================================================
# 2. FEATURE GRAPHIC - 1024x500
# ============================================================
def create_feature_graphic():
    w, h = 1024, 500
    img = Image.new('RGBA', (w, h), BACKGROUND)
    draw = ImageDraw.Draw(img)
    
    # Gradient background
    for y in range(h):
        ratio = y / h
        r = int(0x1A * (1 - ratio) + 0x00 * ratio)
        g = int(0x1A * (1 - ratio) + 0xBF * ratio)
        b = int(0x2E * (1 - ratio) + 0xA6 * ratio)
        draw.line([(0, y), (w, y)], fill=(r, g, b))
    
    # Decorative elements - circles
    draw.ellipse([-200, -100, 300, 200], fill=(0, 191, 166, 30))
    draw.ellipse([700, 300, 1200, 800], fill="#4ECDC4")
    
    # App name
    font_title = get_font(72, bold=True)
    font_subtitle = get_font(32)
    
    # "HabitSehat" text
    title = "HabitSehat"
    subtitle = "Kebiasaan Sehat Harian"
    
    # Centered
    title_bbox = draw.textbbox((0, 0), title, font=font_title)
    title_w = title_bbox[2] - title_bbox[0]
    draw.text(((w - title_w) // 2, 130), title, font=font_title, fill=TEXT_LIGHT)
    
    sub_bbox = draw.textbbox((0, 0), subtitle, font=font_subtitle)
    sub_w = sub_bbox[2] - sub_bbox[0]
    draw.text(((w - sub_w) // 2, 220), subtitle, font=font_subtitle, fill="#E0F2F1")
    
    # Feature tags
    features = [
        ("📋", "Habit Tracker"),
        ("💧", "Water Tracker"),
        ("🔥", "Streak & Heatmap"),
        ("🚬", "HabitStop"),
        ("🍅", "Pomodoro"),
        ("🏆", "Challenges"),
    ]
    
    start_y = 290
    for i, (emoji, text) in enumerate(features):
        col = i % 3
        row = i // 3
        x = 150 + col * 280
        y = start_y + row * 70
        
        # Feature box
        box_w, box_h = 240, 55
        draw.rounded_rectangle([x, y, x + box_w, y + box_h], 
                               radius=16, fill=(255, 255, 255, 200), 
                               outline=(255, 255, 255, 100), width=1)
        
        # Emoji + text
        draw.text((x + 20, y + 8), emoji, font=get_font(28), fill=TEXT_DARK)
        draw.text((x + 70, y + 10), text, font=get_font(20, bold=True), fill=TEXT_DARK)
    
    # Bottom tagline
    tagline = "100% Offline • No Account • Data Privasi"
    tag_font = get_font(22)
    tag_bbox = draw.textbbox((0, 0), tagline, font=tag_font)
    tag_w = tag_bbox[2] - tag_bbox[0]
    draw.text(((w - tag_w) // 2, 440), tagline, font=tag_font, fill="#B2DFDB")
    
    img.save(f"{OUTPUT_DIR}/feature-graphic-1024x500.png", "PNG")
    print("✅ Created feature-graphic-1024x500.png")
    return img

# ============================================================
# 3. PHONE SCREENSHOTS - 1280x720 (for 5" phone)
# ============================================================
def create_phone_screenshot(title, subtitle, features, filename, accent_color=PRIMARY):
    w, h = 1280, 720
    img = Image.new('RGBA', (w, h), BACKGROUND)
    draw = ImageDraw.Draw(img)
    
    # Header bar
    draw.rectangle([0, 0, w, 160], fill=accent_color)
    
    # Title
    font_title = get_font(42, bold=True)
    font_sub = get_font(24)
    draw.text((60, 40), title, font=font_title, fill=TEXT_LIGHT)
    draw.text((60, 100), subtitle, font=font_sub, fill="#E0F2F1")
    
    # Phone frame outline
    phone_x, phone_y = 140, 200
    phone_w, phone_h = 1000, 480
    draw.rounded_rectangle([phone_x, phone_y, phone_x + phone_w, phone_y + phone_h], 
                           radius=40, outline="#333", width=8)
    
    # Screen area
    screen_x, screen_y = phone_x + 20, phone_y + 20
    screen_w, screen_h = phone_w - 40, phone_h - 40
    
    # Feature cards inside
    card_w = (screen_w - 60) // 2
    card_h = (screen_h - 60) // 3
    
    for i, (icon, label, desc) in enumerate(features):
        col = i % 2
        row = i // 2
        if row >= 3:
            continue
        x = screen_x + 20 + col * (card_w + 20)
        y = screen_y + 20 + row * (card_h + 20)
        
        draw.rounded_rectangle([x, y, x + card_w, y + card_h], 
                               radius=20, fill=BACKGROUND, outline="#E0E0E0", width=2)
        
        # Icon
        draw.text((x + 30, y + 30), icon, font=get_font(48), fill=accent_color)
        # Label
        draw.text((x + 30, y + 100), label, font=get_font(24, bold=True), fill=TEXT_DARK)
        # Desc
        draw.text((x + 30, y + 140), desc, font=get_font(18), fill="#757575")
    
    # Bottom indicator
    draw.text((w//2 - 100, h - 60), "▲ Swipe untuk lebih", font=get_font(20), fill="#999")
    
    img.save(f"{OUTPUT_DIR}/{filename}", "PNG")
    print(f"✅ Created {filename}")
    return img

def create_all_screenshots():
    # Screenshot 1: Home - Habit Tracking
    create_phone_screenshot(
        "Habit Tracker",
        "Catat & Lacak Kebiasaan Harian",
        [
            ("📋", "Olahraga", "Target: 30 menit"),
            ("📖", "Membaca", "Target: 20 halaman"),
            ("🧘", "Meditation", "Target: 10 menit"),
            ("💊", "Vitamin", "Setiap pagi"),
            ("🏃", "Lari Pagi", "3x seminggu"),
            ("🥗", "Makan Sehat", "3x sehari"),
        ],
        "screenshot-1-home.png",
        PRIMARY
    )
    
    # Screenshot 2: Water Tracker
    create_phone_screenshot(
        "Water Tracker",
        "Pantau Asupan Air Harian",
        [
            ("💧", "Progress Hari Ini", "1,850 / 2,500 ml"),
            ("☕", "Kopi", "+200 ml"),
            ("🥤", "Air Putih", "+300 ml"),
            ("🍵", "Teh", "+150 ml"),
            ("🎯", "Target: 2.5L", "74% terselesaikan"),
            ("📊", "History Mingguan", "Rata-rata 2.1L"),
        ],
        "screenshot-2-water.png",
        "#4ECDC4"
    )
    
    # Screenshot 3: Heatmap Calendar
    create_phone_screenshot(
        "Heatmap Calendar",
        "Visualisasi Konsistensi Seperti GitHub",
        [
            ("🔥", "Streak Saat Ini", "12 hari berturut"),
            ("📅", "Januari 2025", "28/31 hari aktif"),
            ("🟢", "Hari Lengkap", "Semua habit ✓"),
            ("🟡", "Hari Parsial", "Sebagian habit"),
            ("⚪", "Hari Kosong", "Belum ada log"),
            ("📈", "Total Mingguan", "85% konsistensi"),
        ],
        "screenshot-3-heatmap.png",
        "#4CAF50"
    )
    
    # Screenshot 4: Theme Studio
    create_phone_screenshot(
        "Theme Studio",
        "20+ Tema Premium (5 Gratis)",
        [
            ("🎨", "Mint Fresh", "Default - Gratis"),
            ("🌸", "Sakura", "Premium - Pink"),
            ("🌊", "Ocean", "Premium - Biru"),
            ("🌅", "Sunset", "Premium - Orange"),
            ("🌌", "Midnight", "Premium - Dark"),
            ("⚡", "Cyberpunk", "Premium - Neon"),
        ],
        "screenshot-4-themes.png",
        "#9C27B0"
    )
    
    # Screenshot 5: HabitStop
    create_phone_screenshot(
        "HabitStop",
        "Hentikan Kebiasaan Buruk + Hitung Uang Tersimpan",
        [
            ("🚬", "Merokok", "Tersimpan: Rp2.4jt"),
            ("☕", "Kopi Berlebih", "Tersimpan: Rp800rb"),
            ("🍔", "Junk Food", "Tersimpan: Rp1.2jt"),
            ("📱", "Sosmed Malam", "Streak: 14 hari"),
            ("💰", "Total Tersimpan", "Rp4.4 juta"),
            ("❤️", "Health Timeline", "Paru bersih dlm 3 bln"),
        ],
        "screenshot-5-habitstop.png",
        "#FF5722"
    )
    
    # Screenshot 6: Pomodoro + Challenges
    create_phone_screenshot(
        "Pomodoro & Challenges",
        "Fokus Maksimal + Gamifikasi Kebiasaan",
        [
            ("🍅", "Pomodoro Timer", "25/50/90 menit"),
            ("🎵", "White Noise", "Hujan, Api, Cafè"),
            ("🏆", "Challenge 7 Hari", "Progress: 5/7"),
            ("🌿", "Challenge 21 Hari", "Belum mulai"),
            ("🏅", "Badge: Pemula", "Ter_unlock"),
            ("📊", "Fokus Mingguan", "6 jam 30 menit"),
        ],
        "screenshot-6-pomodoro.png",
        "#FF9800"
    )

# ============================================================
# MAIN
# ============================================================
if __name__ == "__main__":
    print("🎨 Generating Play Store assets for HabitSehat...\n")
    
    create_app_icon()
    print()
    create_feature_graphic()
    print()
    create_all_screenshots()
    print()
    
    print("=" * 50)
    print("✅ ALL ASSETS GENERATED!")
    print(f"📁 Output: {OUTPUT_DIR}")
    print("=" * 50)
    for f in sorted(os.listdir(OUTPUT_DIR)):
        path = os.path.join(OUTPUT_DIR, f)
        size_kb = os.path.getsize(path) / 1024
        print(f"  {f} ({size_kb:.1f} KB)")