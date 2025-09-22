package com.journal.life5to9.utils;

import com.journal.life5to9.data.entity.Category;

import java.util.HashMap;
import java.util.Map;

public class CategoryEmojiMapper {
    
    private static final Map<String, String> EMOJI_MAP = new HashMap<>();
    
    static {
        // Default categories with their emojis
        EMOJI_MAP.put("Fitness", "💪");
        EMOJI_MAP.put("Family", "👨‍👩‍👧‍👦");
        EMOJI_MAP.put("Learning", "📚");
        EMOJI_MAP.put("Entertainment", "🎬");
        EMOJI_MAP.put("Chores", "🏠");
        EMOJI_MAP.put("Social", "👥");
        EMOJI_MAP.put("Rest", "😴");
        
        // Additional common categories
        EMOJI_MAP.put("Work", "💼");
        EMOJI_MAP.put("Exercise", "🏃‍♂️");
        EMOJI_MAP.put("Reading", "📖");
        EMOJI_MAP.put("Gaming", "🎮");
        EMOJI_MAP.put("Cooking", "👨‍🍳");
        EMOJI_MAP.put("Travel", "✈️");
        EMOJI_MAP.put("Shopping", "🛍️");
        EMOJI_MAP.put("Music", "🎵");
        EMOJI_MAP.put("Art", "🎨");
        EMOJI_MAP.put("Sports", "⚽");
        EMOJI_MAP.put("Health", "🏥");
        EMOJI_MAP.put("Study", "📝");
        EMOJI_MAP.put("Hobby", "🎯");
        EMOJI_MAP.put("Volunteer", "🤝");
        EMOJI_MAP.put("Meditation", "🧘‍♂️");
        EMOJI_MAP.put("Gardening", "🌱");
        EMOJI_MAP.put("Photography", "📸");
        EMOJI_MAP.put("Writing", "✍️");
        EMOJI_MAP.put("Dancing", "💃");
        EMOJI_MAP.put("Swimming", "🏊‍♂️");
        EMOJI_MAP.put("Cycling", "🚴‍♂️");
        EMOJI_MAP.put("Yoga", "🧘‍♀️");
        EMOJI_MAP.put("Gym", "🏋️‍♂️");
        EMOJI_MAP.put("Running", "🏃‍♀️");
        EMOJI_MAP.put("Walking", "🚶‍♂️");
        EMOJI_MAP.put("Hiking", "🥾");
        EMOJI_MAP.put("Camping", "⛺");
        EMOJI_MAP.put("Fishing", "🎣");
        EMOJI_MAP.put("Painting", "🖌️");
        EMOJI_MAP.put("Drawing", "✏️");
        EMOJI_MAP.put("Crafting", "🧵");
        EMOJI_MAP.put("Knitting", "🧶");
        EMOJI_MAP.put("Sewing", "🪡");
        EMOJI_MAP.put("Woodworking", "🔨");
        EMOJI_MAP.put("Repair", "🔧");
        EMOJI_MAP.put("Cleaning", "🧹");
        EMOJI_MAP.put("Organizing", "📦");
        EMOJI_MAP.put("Planning", "📋");
        EMOJI_MAP.put("Research", "🔍");
        EMOJI_MAP.put("Online", "💻");
        EMOJI_MAP.put("Phone", "📱");
        EMOJI_MAP.put("Meeting", "🤝");
        EMOJI_MAP.put("Presentation", "📊");
        EMOJI_MAP.put("Training", "🎓");
        EMOJI_MAP.put("Conference", "🏢");
        EMOJI_MAP.put("Networking", "🌐");
        EMOJI_MAP.put("Relaxation", "🛋️");
        EMOJI_MAP.put("Sleep", "😴");
        EMOJI_MAP.put("Nap", "😪");
        EMOJI_MAP.put("Break", "☕");
        EMOJI_MAP.put("Lunch", "🍽️");
        EMOJI_MAP.put("Dinner", "🍴");
        EMOJI_MAP.put("Snack", "🍿");
        EMOJI_MAP.put("Drink", "🥤");
        EMOJI_MAP.put("Party", "🎉");
        EMOJI_MAP.put("Celebration", "🎊");
        EMOJI_MAP.put("Birthday", "🎂");
        EMOJI_MAP.put("Holiday", "🎄");
        EMOJI_MAP.put("Vacation", "🏖️");
        EMOJI_MAP.put("Weekend", "📅");
        EMOJI_MAP.put("Free Time", "⏰");
        EMOJI_MAP.put("Personal", "👤");
        EMOJI_MAP.put("Self Care", "🧴");
        EMOJI_MAP.put("Beauty", "💄");
        EMOJI_MAP.put("Fashion", "👗");
        EMOJI_MAP.put("Shopping", "🛒");
        EMOJI_MAP.put("Grocery", "🛒");
        EMOJI_MAP.put("Errands", "📋");
        EMOJI_MAP.put("Appointment", "📅");
        EMOJI_MAP.put("Doctor", "👨‍⚕️");
        EMOJI_MAP.put("Dentist", "🦷");
        EMOJI_MAP.put("Therapy", "💬");
        EMOJI_MAP.put("Counseling", "🧠");
        EMOJI_MAP.put("Spiritual", "🙏");
        EMOJI_MAP.put("Prayer", "🕯️");
        EMOJI_MAP.put("Meditation", "🧘");
        EMOJI_MAP.put("Mindfulness", "🧠");
        EMOJI_MAP.put("Gratitude", "🙏");
        EMOJI_MAP.put("Journaling", "📔");
        EMOJI_MAP.put("Reflection", "🤔");
        EMOJI_MAP.put("Planning", "📝");
        EMOJI_MAP.put("Goal Setting", "🎯");
        EMOJI_MAP.put("Review", "📊");
        EMOJI_MAP.put("Analysis", "📈");
        EMOJI_MAP.put("Strategy", "♟️");
        EMOJI_MAP.put("Decision", "🤔");
        EMOJI_MAP.put("Problem Solving", "🧩");
        EMOJI_MAP.put("Innovation", "💡");
        EMOJI_MAP.put("Creativity", "🎨");
        EMOJI_MAP.put("Inspiration", "✨");
        EMOJI_MAP.put("Motivation", "🚀");
        EMOJI_MAP.put("Achievement", "🏆");
        EMOJI_MAP.put("Success", "✅");
        EMOJI_MAP.put("Progress", "📈");
        EMOJI_MAP.put("Growth", "🌱");
        EMOJI_MAP.put("Development", "🔧");
        EMOJI_MAP.put("Improvement", "⬆️");
        EMOJI_MAP.put("Change", "🔄");
        EMOJI_MAP.put("Transformation", "🦋");
        EMOJI_MAP.put("New", "🆕");
        EMOJI_MAP.put("Fresh", "🌿");
        EMOJI_MAP.put("Clean", "✨");
        EMOJI_MAP.put("Pure", "🤍");
        EMOJI_MAP.put("Simple", "🔸");
        EMOJI_MAP.put("Easy", "😊");
        EMOJI_MAP.put("Fun", "😄");
        EMOJI_MAP.put("Happy", "😊");
        EMOJI_MAP.put("Joy", "😄");
        EMOJI_MAP.put("Love", "❤️");
        EMOJI_MAP.put("Peace", "☮️");
        EMOJI_MAP.put("Calm", "😌");
        EMOJI_MAP.put("Serene", "🌊");
        EMOJI_MAP.put("Tranquil", "🌸");
        EMOJI_MAP.put("Quiet", "🤫");
        EMOJI_MAP.put("Silent", "🔇");
        EMOJI_MAP.put("Alone", "👤");
        EMOJI_MAP.put("Solo", "🎯");
        EMOJI_MAP.put("Independent", "🦅");
        EMOJI_MAP.put("Free", "🆓");
        EMOJI_MAP.put("Wild", "🌿");
        EMOJI_MAP.put("Adventure", "🗺️");
        EMOJI_MAP.put("Explore", "🔍");
        EMOJI_MAP.put("Discover", "💎");
        EMOJI_MAP.put("Find", "🔍");
        EMOJI_MAP.put("Search", "🔎");
        EMOJI_MAP.put("Look", "👀");
        EMOJI_MAP.put("See", "👁️");
        EMOJI_MAP.put("Watch", "👀");
        EMOJI_MAP.put("Listen", "👂");
        EMOJI_MAP.put("Hear", "👂");
        EMOJI_MAP.put("Feel", "🤲");
        EMOJI_MAP.put("Touch", "✋");
        EMOJI_MAP.put("Smell", "👃");
        EMOJI_MAP.put("Taste", "👅");
        EMOJI_MAP.put("Experience", "🌟");
        EMOJI_MAP.put("Live", "❤️");
        EMOJI_MAP.put("Breathe", "🫁");
        EMOJI_MAP.put("Exist", "🌍");
        EMOJI_MAP.put("Be", "✨");
        EMOJI_MAP.put("Present", "🎁");
        EMOJI_MAP.put("Now", "⏰");
        EMOJI_MAP.put("Today", "📅");
        EMOJI_MAP.put("Tomorrow", "🌅");
        EMOJI_MAP.put("Yesterday", "🌙");
        EMOJI_MAP.put("Past", "⏪");
        EMOJI_MAP.put("Future", "⏩");
        EMOJI_MAP.put("Time", "⏰");
        EMOJI_MAP.put("Moment", "⏱️");
        EMOJI_MAP.put("Second", "⏱️");
        EMOJI_MAP.put("Minute", "⏰");
        EMOJI_MAP.put("Hour", "🕐");
        EMOJI_MAP.put("Day", "📅");
        EMOJI_MAP.put("Week", "📆");
        EMOJI_MAP.put("Month", "📅");
        EMOJI_MAP.put("Year", "🗓️");
        EMOJI_MAP.put("Season", "🍂");
        EMOJI_MAP.put("Spring", "🌸");
        EMOJI_MAP.put("Summer", "☀️");
        EMOJI_MAP.put("Autumn", "🍂");
        EMOJI_MAP.put("Winter", "❄️");
        EMOJI_MAP.put("Weather", "🌤️");
        EMOJI_MAP.put("Sunny", "☀️");
        EMOJI_MAP.put("Cloudy", "☁️");
        EMOJI_MAP.put("Rainy", "🌧️");
        EMOJI_MAP.put("Snowy", "❄️");
        EMOJI_MAP.put("Windy", "💨");
        EMOJI_MAP.put("Stormy", "⛈️");
        EMOJI_MAP.put("Foggy", "🌫️");
        EMOJI_MAP.put("Clear", "☀️");
        EMOJI_MAP.put("Bright", "☀️");
        EMOJI_MAP.put("Dark", "🌙");
        EMOJI_MAP.put("Light", "💡");
        EMOJI_MAP.put("Shadow", "🌑");
        EMOJI_MAP.put("Shade", "🌿");
        EMOJI_MAP.put("Cool", "❄️");
        EMOJI_MAP.put("Warm", "🔥");
        EMOJI_MAP.put("Hot", "🌶️");
        EMOJI_MAP.put("Cold", "🧊");
        EMOJI_MAP.put("Fresh", "🌿");
        EMOJI_MAP.put("New", "🆕");
        EMOJI_MAP.put("Old", "🕰️");
        EMOJI_MAP.put("Ancient", "🏛️");
        EMOJI_MAP.put("Modern", "🏢");
        EMOJI_MAP.put("Contemporary", "🎨");
        EMOJI_MAP.put("Classic", "📚");
        EMOJI_MAP.put("Traditional", "🏮");
        EMOJI_MAP.put("Cultural", "🎭");
        EMOJI_MAP.put("Artistic", "🎨");
        EMOJI_MAP.put("Creative", "✨");
        EMOJI_MAP.put("Imaginative", "🧠");
        EMOJI_MAP.put("Fantasy", "🧚");
        EMOJI_MAP.put("Magic", "🪄");
        EMOJI_MAP.put("Wonder", "✨");
        EMOJI_MAP.put("Mystery", "🔍");
        EMOJI_MAP.put("Secret", "🤫");
        EMOJI_MAP.put("Hidden", "🕳️");
        EMOJI_MAP.put("Unknown", "❓");
        EMOJI_MAP.put("Question", "❓");
        EMOJI_MAP.put("Answer", "💡");
        EMOJI_MAP.put("Solution", "✅");
        EMOJI_MAP.put("Problem", "❌");
        EMOJI_MAP.put("Issue", "⚠️");
        EMOJI_MAP.put("Challenge", "🏔️");
        EMOJI_MAP.put("Obstacle", "🚧");
        EMOJI_MAP.put("Barrier", "🚧");
        EMOJI_MAP.put("Wall", "🧱");
        EMOJI_MAP.put("Door", "🚪");
        EMOJI_MAP.put("Window", "🪟");
        EMOJI_MAP.put("Opening", "🕳️");
        EMOJI_MAP.put("Closing", "🔒");
        EMOJI_MAP.put("Start", "🚀");
        EMOJI_MAP.put("Begin", "🎬");
        EMOJI_MAP.put("End", "🏁");
        EMOJI_MAP.put("Finish", "✅");
        EMOJI_MAP.put("Complete", "🎯");
        EMOJI_MAP.put("Done", "✅");
        EMOJI_MAP.put("Success", "🏆");
        EMOJI_MAP.put("Victory", "🏆");
        EMOJI_MAP.put("Win", "🏆");
        EMOJI_MAP.put("Lose", "😞");
        EMOJI_MAP.put("Fail", "❌");
        EMOJI_MAP.put("Error", "⚠️");
        EMOJI_MAP.put("Mistake", "❌");
        EMOJI_MAP.put("Accident", "💥");
        EMOJI_MAP.put("Incident", "⚠️");
        EMOJI_MAP.put("Event", "📅");
        EMOJI_MAP.put("Occasion", "🎉");
        EMOJI_MAP.put("Celebration", "🎊");
        EMOJI_MAP.put("Party", "🎉");
        EMOJI_MAP.put("Gathering", "👥");
        EMOJI_MAP.put("Meeting", "🤝");
        EMOJI_MAP.put("Conference", "🏢");
        EMOJI_MAP.put("Summit", "🏔️");
        EMOJI_MAP.put("Assembly", "👥");
        EMOJI_MAP.put("Convention", "🏢");
        EMOJI_MAP.put("Exhibition", "🎨");
        EMOJI_MAP.put("Show", "🎭");
        EMOJI_MAP.put("Performance", "🎭");
        EMOJI_MAP.put("Concert", "🎵");
        EMOJI_MAP.put("Festival", "🎪");
        EMOJI_MAP.put("Carnival", "🎠");
        EMOJI_MAP.put("Fair", "🎡");
        EMOJI_MAP.put("Market", "🏪");
        EMOJI_MAP.put("Store", "🏪");
        EMOJI_MAP.put("Shop", "🛍️");
        EMOJI_MAP.put("Mall", "🏬");
        EMOJI_MAP.put("Center", "🏢");
        EMOJI_MAP.put("Building", "🏢");
        EMOJI_MAP.put("House", "🏠");
        EMOJI_MAP.put("Home", "🏠");
        EMOJI_MAP.put("Apartment", "🏢");
        EMOJI_MAP.put("Room", "🚪");
        EMOJI_MAP.put("Space", "🌌");
        EMOJI_MAP.put("Place", "📍");
        EMOJI_MAP.put("Location", "📍");
        EMOJI_MAP.put("Position", "📍");
        EMOJI_MAP.put("Spot", "📍");
        EMOJI_MAP.put("Point", "📍");
        EMOJI_MAP.put("Area", "🗺️");
        EMOJI_MAP.put("Region", "🗺️");
        EMOJI_MAP.put("Zone", "🗺️");
        EMOJI_MAP.put("District", "🏘️");
        EMOJI_MAP.put("Neighborhood", "🏘️");
        EMOJI_MAP.put("Community", "👥");
        EMOJI_MAP.put("Society", "👥");
        EMOJI_MAP.put("Group", "👥");
        EMOJI_MAP.put("Team", "👥");
        EMOJI_MAP.put("Squad", "👥");
        EMOJI_MAP.put("Crew", "👥");
        EMOJI_MAP.put("Gang", "👥");
        EMOJI_MAP.put("Club", "🎯");
        EMOJI_MAP.put("Organization", "🏢");
        EMOJI_MAP.put("Company", "🏢");
        EMOJI_MAP.put("Business", "💼");
        EMOJI_MAP.put("Enterprise", "🏢");
        EMOJI_MAP.put("Corporation", "🏢");
        EMOJI_MAP.put("Firm", "🏢");
        EMOJI_MAP.put("Agency", "🏢");
        EMOJI_MAP.put("Bureau", "🏢");
        EMOJI_MAP.put("Office", "🏢");
        EMOJI_MAP.put("Department", "🏢");
        EMOJI_MAP.put("Division", "🏢");
        EMOJI_MAP.put("Section", "📋");
        EMOJI_MAP.put("Unit", "📦");
        EMOJI_MAP.put("Part", "🧩");
        EMOJI_MAP.put("Piece", "🧩");
        EMOJI_MAP.put("Fragment", "🧩");
        EMOJI_MAP.put("Bit", "🧩");
        EMOJI_MAP.put("Chunk", "🧩");
        EMOJI_MAP.put("Slice", "🍕");
        EMOJI_MAP.put("Portion", "🍽️");
        EMOJI_MAP.put("Share", "🤝");
        EMOJI_MAP.put("Piece", "🧩");
        EMOJI_MAP.put("Element", "🧩");
        EMOJI_MAP.put("Component", "🧩");
        EMOJI_MAP.put("Ingredient", "🧩");
        EMOJI_MAP.put("Factor", "🧩");
        EMOJI_MAP.put("Aspect", "🧩");
        EMOJI_MAP.put("Feature", "✨");
        EMOJI_MAP.put("Characteristic", "✨");
        EMOJI_MAP.put("Trait", "✨");
        EMOJI_MAP.put("Quality", "✨");
        EMOJI_MAP.put("Property", "✨");
        EMOJI_MAP.put("Attribute", "✨");
        EMOJI_MAP.put("Value", "💎");
        EMOJI_MAP.put("Worth", "💎");
        EMOJI_MAP.put("Price", "💰");
        EMOJI_MAP.put("Cost", "💰");
        EMOJI_MAP.put("Expense", "💰");
        EMOJI_MAP.put("Investment", "💰");
        EMOJI_MAP.put("Return", "💰");
        EMOJI_MAP.put("Profit", "💰");
        EMOJI_MAP.put("Loss", "💰");
        EMOJI_MAP.put("Gain", "📈");
        EMOJI_MAP.put("Benefit", "✅");
        EMOJI_MAP.put("Advantage", "✅");
        EMOJI_MAP.put("Disadvantage", "❌");
        EMOJI_MAP.put("Drawback", "❌");
        EMOJI_MAP.put("Limitation", "❌");
        EMOJI_MAP.put("Restriction", "❌");
        EMOJI_MAP.put("Constraint", "❌");
        EMOJI_MAP.put("Boundary", "🚧");
        EMOJI_MAP.put("Limit", "🚧");
        EMOJI_MAP.put("Edge", "🔪");
        EMOJI_MAP.put("Border", "🚧");
        EMOJI_MAP.put("Margin", "📄");
        EMOJI_MAP.put("Padding", "📄");
        EMOJI_MAP.put("Space", "🌌");
        EMOJI_MAP.put("Gap", "🕳️");
        EMOJI_MAP.put("Hole", "🕳️");
        EMOJI_MAP.put("Opening", "🕳️");
        EMOJI_MAP.put("Entrance", "🚪");
        EMOJI_MAP.put("Exit", "🚪");
        EMOJI_MAP.put("Door", "🚪");
        EMOJI_MAP.put("Gate", "🚪");
        EMOJI_MAP.put("Portal", "🚪");
        EMOJI_MAP.put("Path", "🛤️");
        EMOJI_MAP.put("Way", "🛤️");
        EMOJI_MAP.put("Route", "🛤️");
        EMOJI_MAP.put("Road", "🛣️");
        EMOJI_MAP.put("Street", "🛣️");
        EMOJI_MAP.put("Avenue", "🛣️");
        EMOJI_MAP.put("Boulevard", "🛣️");
        EMOJI_MAP.put("Lane", "🛣️");
        EMOJI_MAP.put("Alley", "🛣️");
        EMOJI_MAP.put("Drive", "🛣️");
        EMOJI_MAP.put("Court", "🛣️");
        EMOJI_MAP.put("Place", "📍");
        EMOJI_MAP.put("Square", "⬜");
        EMOJI_MAP.put("Circle", "⭕");
        EMOJI_MAP.put("Triangle", "🔺");
        EMOJI_MAP.put("Rectangle", "⬜");
        EMOJI_MAP.put("Square", "⬜");
        EMOJI_MAP.put("Diamond", "💎");
        EMOJI_MAP.put("Star", "⭐");
        EMOJI_MAP.put("Heart", "❤️");
        EMOJI_MAP.put("Moon", "🌙");
        EMOJI_MAP.put("Sun", "☀️");
        EMOJI_MAP.put("Earth", "🌍");
        EMOJI_MAP.put("World", "🌍");
        EMOJI_MAP.put("Globe", "🌍");
        EMOJI_MAP.put("Planet", "🪐");
        EMOJI_MAP.put("Universe", "🌌");
        EMOJI_MAP.put("Space", "🌌");
        EMOJI_MAP.put("Galaxy", "🌌");
        EMOJI_MAP.put("Star", "⭐");
        EMOJI_MAP.put("Constellation", "⭐");
        EMOJI_MAP.put("Asteroid", "☄️");
        EMOJI_MAP.put("Comet", "☄️");
        EMOJI_MAP.put("Meteor", "☄️");
        EMOJI_MAP.put("Shooting Star", "☄️");
        EMOJI_MAP.put("Wish", "⭐");
        EMOJI_MAP.put("Dream", "💭");
        EMOJI_MAP.put("Hope", "✨");
        EMOJI_MAP.put("Faith", "🙏");
        EMOJI_MAP.put("Belief", "🙏");
        EMOJI_MAP.put("Trust", "🤝");
        EMOJI_MAP.put("Confidence", "💪");
        EMOJI_MAP.put("Courage", "🦁");
        EMOJI_MAP.put("Bravery", "🦁");
        EMOJI_MAP.put("Fear", "😨");
        EMOJI_MAP.put("Worry", "😰");
        EMOJI_MAP.put("Anxiety", "😰");
        EMOJI_MAP.put("Stress", "😰");
        EMOJI_MAP.put("Pressure", "😰");
        EMOJI_MAP.put("Tension", "😰");
        EMOJI_MAP.put("Strain", "😰");
        EMOJI_MAP.put("Burden", "😰");
        EMOJI_MAP.put("Load", "😰");
        EMOJI_MAP.put("Weight", "⚖️");
        EMOJI_MAP.put("Heavy", "⚖️");
        EMOJI_MAP.put("Light", "💡");
        EMOJI_MAP.put("Bright", "☀️");
        EMOJI_MAP.put("Dark", "🌙");
        EMOJI_MAP.put("Shadow", "🌑");
        EMOJI_MAP.put("Shade", "🌿");
        EMOJI_MAP.put("Color", "🎨");
        EMOJI_MAP.put("Red", "🔴");
        EMOJI_MAP.put("Blue", "🔵");
        EMOJI_MAP.put("Green", "🟢");
        EMOJI_MAP.put("Yellow", "🟡");
        EMOJI_MAP.put("Orange", "🟠");
        EMOJI_MAP.put("Purple", "🟣");
        EMOJI_MAP.put("Pink", "🩷");
        EMOJI_MAP.put("Brown", "🤎");
        EMOJI_MAP.put("Black", "⚫");
        EMOJI_MAP.put("White", "⚪");
        EMOJI_MAP.put("Gray", "⚪");
        EMOJI_MAP.put("Silver", "⚪");
        EMOJI_MAP.put("Gold", "🟡");
        EMOJI_MAP.put("Bronze", "🟤");
        EMOJI_MAP.put("Copper", "🟤");
        EMOJI_MAP.put("Metal", "🔩");
        EMOJI_MAP.put("Iron", "🔩");
        EMOJI_MAP.put("Steel", "🔩");
        EMOJI_MAP.put("Aluminum", "🔩");
        EMOJI_MAP.put("Tin", "🔩");
        EMOJI_MAP.put("Lead", "🔩");
        EMOJI_MAP.put("Zinc", "🔩");
        EMOJI_MAP.put("Nickel", "🔩");
        EMOJI_MAP.put("Chrome", "🔩");
        EMOJI_MAP.put("Titanium", "🔩");
        EMOJI_MAP.put("Platinum", "🔩");
        EMOJI_MAP.put("Palladium", "🔩");
        EMOJI_MAP.put("Rhodium", "🔩");
        EMOJI_MAP.put("Iridium", "🔩");
        EMOJI_MAP.put("Osmium", "🔩");
        EMOJI_MAP.put("Ruthenium", "🔩");
        EMOJI_MAP.put("Rhenium", "🔩");
        EMOJI_MAP.put("Tungsten", "🔩");
        EMOJI_MAP.put("Molybdenum", "🔩");
        EMOJI_MAP.put("Tantalum", "🔩");
        EMOJI_MAP.put("Hafnium", "🔩");
        EMOJI_MAP.put("Zirconium", "🔩");
        EMOJI_MAP.put("Niobium", "🔩");
        EMOJI_MAP.put("Vanadium", "🔩");
        EMOJI_MAP.put("Chromium", "🔩");
        EMOJI_MAP.put("Manganese", "🔩");
        EMOJI_MAP.put("Cobalt", "🔩");
        EMOJI_MAP.put("Nickel", "🔩");
        EMOJI_MAP.put("Copper", "🟤");
        EMOJI_MAP.put("Zinc", "🔩");
        EMOJI_MAP.put("Gallium", "🔩");
        EMOJI_MAP.put("Germanium", "🔩");
        EMOJI_MAP.put("Arsenic", "🔩");
        EMOJI_MAP.put("Selenium", "🔩");
        EMOJI_MAP.put("Bromine", "🔩");
        EMOJI_MAP.put("Krypton", "🔩");
        EMOJI_MAP.put("Rubidium", "🔩");
        EMOJI_MAP.put("Strontium", "🔩");
        EMOJI_MAP.put("Yttrium", "🔩");
        EMOJI_MAP.put("Zirconium", "🔩");
        EMOJI_MAP.put("Niobium", "🔩");
        EMOJI_MAP.put("Molybdenum", "🔩");
        EMOJI_MAP.put("Technetium", "🔩");
        EMOJI_MAP.put("Ruthenium", "🔩");
        EMOJI_MAP.put("Rhodium", "🔩");
        EMOJI_MAP.put("Palladium", "🔩");
        EMOJI_MAP.put("Silver", "⚪");
        EMOJI_MAP.put("Cadmium", "🔩");
        EMOJI_MAP.put("Indium", "🔩");
        EMOJI_MAP.put("Tin", "🔩");
        EMOJI_MAP.put("Antimony", "🔩");
        EMOJI_MAP.put("Tellurium", "🔩");
        EMOJI_MAP.put("Iodine", "🔩");
        EMOJI_MAP.put("Xenon", "🔩");
        EMOJI_MAP.put("Cesium", "🔩");
        EMOJI_MAP.put("Barium", "🔩");
        EMOJI_MAP.put("Lanthanum", "🔩");
        EMOJI_MAP.put("Cerium", "🔩");
        EMOJI_MAP.put("Praseodymium", "🔩");
        EMOJI_MAP.put("Neodymium", "🔩");
        EMOJI_MAP.put("Promethium", "🔩");
        EMOJI_MAP.put("Samarium", "🔩");
        EMOJI_MAP.put("Europium", "🔩");
        EMOJI_MAP.put("Gadolinium", "🔩");
        EMOJI_MAP.put("Terbium", "🔩");
        EMOJI_MAP.put("Dysprosium", "🔩");
        EMOJI_MAP.put("Holmium", "🔩");
        EMOJI_MAP.put("Erbium", "🔩");
        EMOJI_MAP.put("Thulium", "🔩");
        EMOJI_MAP.put("Ytterbium", "🔩");
        EMOJI_MAP.put("Lutetium", "🔩");
        EMOJI_MAP.put("Hafnium", "🔩");
        EMOJI_MAP.put("Tantalum", "🔩");
        EMOJI_MAP.put("Tungsten", "🔩");
        EMOJI_MAP.put("Rhenium", "🔩");
        EMOJI_MAP.put("Osmium", "🔩");
        EMOJI_MAP.put("Iridium", "🔩");
        EMOJI_MAP.put("Platinum", "🔩");
        EMOJI_MAP.put("Gold", "🟡");
        EMOJI_MAP.put("Mercury", "🔩");
        EMOJI_MAP.put("Thallium", "🔩");
        EMOJI_MAP.put("Lead", "🔩");
        EMOJI_MAP.put("Bismuth", "🔩");
        EMOJI_MAP.put("Polonium", "🔩");
        EMOJI_MAP.put("Astatine", "🔩");
        EMOJI_MAP.put("Radon", "🔩");
        EMOJI_MAP.put("Francium", "🔩");
        EMOJI_MAP.put("Radium", "🔩");
        EMOJI_MAP.put("Actinium", "🔩");
        EMOJI_MAP.put("Thorium", "🔩");
        EMOJI_MAP.put("Protactinium", "🔩");
        EMOJI_MAP.put("Uranium", "🔩");
        EMOJI_MAP.put("Neptunium", "🔩");
        EMOJI_MAP.put("Plutonium", "🔩");
        EMOJI_MAP.put("Americium", "🔩");
        EMOJI_MAP.put("Curium", "🔩");
        EMOJI_MAP.put("Berkelium", "🔩");
        EMOJI_MAP.put("Californium", "🔩");
        EMOJI_MAP.put("Einsteinium", "🔩");
        EMOJI_MAP.put("Fermium", "🔩");
        EMOJI_MAP.put("Mendelevium", "🔩");
        EMOJI_MAP.put("Nobelium", "🔩");
        EMOJI_MAP.put("Lawrencium", "🔩");
        EMOJI_MAP.put("Rutherfordium", "🔩");
        EMOJI_MAP.put("Dubnium", "🔩");
        EMOJI_MAP.put("Seaborgium", "🔩");
        EMOJI_MAP.put("Bohrium", "🔩");
        EMOJI_MAP.put("Hassium", "🔩");
        EMOJI_MAP.put("Meitnerium", "🔩");
        EMOJI_MAP.put("Darmstadtium", "🔩");
        EMOJI_MAP.put("Roentgenium", "🔩");
        EMOJI_MAP.put("Copernicium", "🔩");
        EMOJI_MAP.put("Nihonium", "🔩");
        EMOJI_MAP.put("Flerovium", "🔩");
        EMOJI_MAP.put("Moscovium", "🔩");
        EMOJI_MAP.put("Livermorium", "🔩");
        EMOJI_MAP.put("Tennessine", "🔩");
        EMOJI_MAP.put("Oganesson", "🔩");
    }
    
    /**
     * Get emoji for a category name
     * @param categoryName The name of the category
     * @return The emoji for the category, or a default emoji if not found
     */
    public static String getEmojiForCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return "📝"; // Default emoji for empty/null categories
        }
        
        // Try exact match first
        String emoji = EMOJI_MAP.get(categoryName.trim());
        if (emoji != null) {
            return emoji;
        }
        
        // Try case-insensitive match
        for (Map.Entry<String, String> entry : EMOJI_MAP.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(categoryName.trim())) {
                return entry.getValue();
            }
        }
        
        // Try partial match (contains)
        String lowerCategoryName = categoryName.toLowerCase().trim();
        for (Map.Entry<String, String> entry : EMOJI_MAP.entrySet()) {
            if (entry.getKey().toLowerCase().contains(lowerCategoryName) || 
                lowerCategoryName.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        
        // Default emoji if no match found
        return "📝";
    }
    
    /**
     * Get emoji for a category object
     * @param category The category object
     * @return The emoji for the category
     */
    public static String getEmojiForCategory(Category category) {
        if (category == null || category.getName() == null) {
            return "📝";
        }
        return getEmojiForCategory(category.getName());
    }
    
    /**
     * Get all available emojis
     * @return A map of category names to emojis
     */
    public static Map<String, String> getAllEmojis() {
        return new HashMap<>(EMOJI_MAP);
    }
    
    /**
     * Check if a category has an emoji
     * @param categoryName The name of the category
     * @return True if the category has an emoji, false otherwise
     */
    public static boolean hasEmoji(String categoryName) {
        return EMOJI_MAP.containsKey(categoryName) || 
               EMOJI_MAP.keySet().stream()
                   .anyMatch(key -> key.equalsIgnoreCase(categoryName));
    }
}
