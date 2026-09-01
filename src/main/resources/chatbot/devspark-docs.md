# DevSpark (LittleSparks) — Childcare Management System

DevSpark is a web app used by a childcare center to manage children, staff, attendance,
activities, meals, learning progress, and communication between the center and parents.
There are three roles: PARENT, TEACHER, and ADMIN. Each role has its own portal and only
sees pages relevant to that role.

## PARENT role

Parents can only see and manage their own enrolled children.

- **Dashboard** (`/parent/dashboard`) — shows number of enrolled children, outstanding fees,
  unread messages, a shortcut to the Progress Report, and recent activity/announcements.
- **Progress Report** (`/parent/progress`) — select one of their children and a date range,
  then click "Update View" to see:
  - Days Present, Activities Completed
  - Average Performance (overall grading percentage, broken down into 4 performance levels)
  - Meals Eaten (Full / Partial / None, with percentage breakdown)
  - Activity Engagement chart (hours spent per activity per day)
  - Attendance Rate chart (present vs absent days)
- **My Children** (`/parent/children`) — list and details of their enrolled children.
- **Messaging** (`/parent/messaging`) — send and receive direct messages with their
  child's teacher. Unread message counts also show as a badge on the Dashboard.
- **Profile** (`/parent/profile`) — update contact details, view/change password.

Parents cannot see other children, cannot see other parents' data, and cannot access
Teacher or Admin pages.

## TEACHER role

Teachers manage the children in the activities/classes assigned to them.

- **Dashboard** (`/teacher/dashboard`) — shows assigned activities, attendance status, and
  quick actions for the day.
- Teachers record attendance for children, log meal consumption (Full/Partial/None per
  child per meal), and grade each child's participation in an activity using a 4-level
  grading scale (Level 1 = weakest, Level 4 = excellent).
- **Parent Comms** — a card on the Teacher Dashboard showing the most recent incoming
  messages from parents. Click it (or go to **Messaging** in the sidebar,
  `/teacher/messages`) to read and reply to a parent's messages about their child.
- **Safety Alerts** — a card on the Teacher Dashboard listing children with a registered
  medical condition or allergy that needs attention.

Teachers cannot access Admin-only pages such as account management or the admin-wide
learning progress dashboard, and cannot view children who are not assigned to their
activities.

## ADMIN role

Admins have center-wide visibility across all children, teachers, and parents.

- **Dashboard** (`/admin/dashboard`) — overall center summary.
- **Learning Progress** (`/admin/learning`) — center-wide view for a specific date:
  - Daily Progress chart: how many children scored at each of the 4 grading levels
    across the whole center that day.
  - Daily Activity List: every activity run that day, which teacher ran it, how many
    children participated.
  - Per-child report section: pick any child and a date range to see the same
    Activity Engagement and Attendance charts available on the parent side.
- Admins can manage teacher and parent accounts, and review registration/teacher
  requests.

Only an Admin can see data across multiple children/teachers at once; Teachers and
Parents only ever see their own scoped data.

## General notes

- If a user asks about a page or action that belongs to a different role than their own,
  they need to log out and log back in through that role's portal to access it — you
  cannot switch roles from within the current session.
- Attendance, meals, and activity grading are recorded by Teachers and then become
  visible to Parents (for their own child) and Admins (center-wide).
