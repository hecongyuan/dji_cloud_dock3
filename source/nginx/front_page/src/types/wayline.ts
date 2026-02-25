// 航线类型
export enum WaylineType {
  NormalWaypointWayline = 0, // 普通航点航线
  AccurateReshootingWayline = 1 // 精准复拍航线
}

export interface WaylineFile {
  id: string,
  name: string,
  sign: string,
  favorited: boolean,
  drone_model_key: string,
  payload_model_keys: string[],
  template_types: number[],
  object_key: string,
  user_name: string,
  update_time: number,
  create_time: number,
}
