//
//  ILSession.h
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ILSession : NSObject

@property NSInteger sectionID;
@property NSString *name;
@property NSString *description;

@property NSMutableArray *questions;
@property NSMutableArray *responses;

@end
