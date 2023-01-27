package spacetime

type SpaceTime struct {
	Id        int     `db:"id"`
	Time      string  `db:"time"`
	Latitude  float64 `db:"latitude"`
	Longitude float64 `db:"longitude"`
	Altitude  float64 `db:"altitude"`
	ObjId     int     `db:"obj_id"`
}
