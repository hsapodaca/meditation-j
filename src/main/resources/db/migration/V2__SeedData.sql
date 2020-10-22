-- J, first entity
insert into entities (name, summary, script, type)
values ('J', 'J is someone who has helped me once. If you are struggling with stress, they will guide you through a breathing exercise to help you relax.','Hi, my name is J.
[Pause.]
How are you today?
[Pause.]
I would like to walk you through a brief exercise to help you relax.', 'Friend');

insert into entities (name, summary, script, type)
values ('Three Deep Breaths', 'This is a breathing exercise to help you relax in a stressful situation.',
'Hello.
[Pause.]
Today we will try a simple breathing exercise.
[Pause two seconds.]
Allow your body to settle into a comfortable position.
[Pause.]
You may keep your eyes open slightly.
[Pause.]
Focus on these words and allow them to be.
[Pause 2 seconds.]
Breathe in the deepest breath you had to take today.
[Pause one second].
In...
[Pause 3 seconds.]
And out.
[Pause.]
Allow your spine to straighten and your shoulders to fall back.
[Pause 3 seconds.]
Today we will practice breathing.
[Pause.]
Start by breathing in gently...
[Pause.]
Focus on the sensation of air passing over your nosrils.
[Pause 2 seconds.]
Notice the air in your chest and belly.
[Pause.]
Notice the inhale end.
[Pause.]
Notice the sensations in the body as the slow exhale begins.
[Pause 3 seconds.]
And rest.
[Pause.]
Let us begin again.
[Pause 1 second.]
Gentle, slow inhale keeping your attention on the air. In...
[Pause 2 seconds.]
And out.
[Pause.]
We will continue one more time.
[Pause.]
In.
[Pause 3 seconds.]
And out.
[Pause 2 seconds.]
And again.
[Pause.]
.....
[Pause 10 seconds.]
....
[Pause 10 seconds.]
...
[Pause 10 seconds.]
..
[Pause 10 seconds.]
.
[Pause 10 seconds.]

[Pause 5 seconds.]
And gently focus on your surroundings.
[Pause.]
Welcome back, friend.', 'Meditation');

insert into entity_relationships (primary_entity_id, target_entity_id, type) values (1, 2, 'FriendHasMeditation');